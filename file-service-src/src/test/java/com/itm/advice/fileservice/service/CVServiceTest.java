package com.itm.advice.fileservice.service;

import com.itm.advice.fileservice.BaseUnitTest;
import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.exception.FileProcessingException;
import com.itm.advice.fileservice.exception.InvalidIDException;
import com.itm.advice.fileservice.exception.NotFoundException;
import com.itm.advice.fileservice.kafka.producer.CvEventProducer;
import com.itm.advice.fileservice.mapper.CVMapper;
import com.itm.advice.fileservice.repository.CVRepository;
import com.itm.advice.fileservice.response.CVResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import com.itm.advice.fileservice.service.impl.CVServiceImpl;
import com.itm.advice.fileservice.util.PageUtils;
import com.itm.advice.fileservice.util.SecurityUtil;
import com.itm.space.itmadvicecommonmodels.kafka.model.CvEvent;
import org.bson.types.Binary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CVServiceTest extends BaseUnitTest {

    @InjectMocks
    private CVServiceImpl cvService;

    @Mock
    private CVRepository cvRepository;

    @Mock
    private CVMapper cvMapper;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private CvEventProducer cvEventProducer;

    private final UUID Id = UUID.fromString("c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000");

    @Test
    @DisplayName("Правильное выполнение метода getCVById")
    void shouldGetCVByIdSuccessfully() {

        CV cv = new CV();
        cv.setId(Id);

        CVResponse cvResponse = new CVResponse();

        when(cvRepository.findById(cv.getId())).thenReturn(Optional.of(cv));
        when(cvMapper.toCVResponse(cv)).thenReturn(cvResponse);

        cvService.getCVById(cv.getId());

        verify(cvRepository, times(1)).findById(Id);
        verify(cvMapper, times(1)).toCVResponse(cv);
    }

    @Test
    @DisplayName("Метод getCVById выбрасывает NotFoundException, если CV не найдено")
    void shouldThrowNotFoundExceptionWhenCVNotFound() {
        when(cvRepository.findById(Id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> cvService.getCVById(Id));

        assertEquals("Резюме не найдено", exception.getMessage());
        verify(cvRepository, times(1)).findById(Id);
        verifyNoInteractions(cvMapper);
    }

    @Test
    @DisplayName("Успешное сохранение CV через saveCv")
    void shouldSaveCvSuccessfully() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-cv.pdf", "application/pdf", "This is a test CV".getBytes());

        CV savedCv = new CV();
        savedCv.setId(Id);
        savedCv.setFile(new Binary(mockFile.getBytes()));

        when(securityUtil.getCurrentUserId()).thenReturn(Id);
        when(cvRepository.save(any(CV.class))).thenReturn(savedCv);

        UUID result = cvService.saveOrReplaceCv(mockFile);

        assertEquals(Id, result);
        verify(cvRepository, times(1)).save(any(CV.class));

        ArgumentCaptor<CV> captor = ArgumentCaptor.forClass(CV.class);
        verify(cvRepository).save(captor.capture());
        CV capturedCV = captor.getValue();
        assertEquals("test-cv.pdf", capturedCV.getTitle());
        assertEquals(Id, capturedCV.getUserId());
    }

    @Test
    @DisplayName("Отправка события в Kafka при успешном сохранении CV")
    void shouldProduceKafkaEventOnSaveCv(){
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-cv.pdf", "application/pdf", "This is a test CV".getBytes());

        when(securityUtil.getCurrentUserId()).thenReturn(Id);
        when(cvRepository.save(any(CV.class))).thenReturn(new CV());

        cvService.saveOrReplaceCv(mockFile);

        verify(cvEventProducer).produce(any(CvEvent.class));
    }



    @Test
    @DisplayName("Метод saveCv выбрасывает FileProcessingException при ошибке обработки файла")
    void shouldThrowFileProcessingExceptionWhenFileIsInvalid() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-cv.pdf", "application/pdf", (byte[]) null);

        FileProcessingException exception = Assertions.assertThrows(FileProcessingException.class, () -> cvService.saveOrReplaceCv(mockFile));

        assertEquals("Файл резюме пустой", exception.getMessage());
        verifyNoInteractions(cvRepository);
    }

    @Test
    @DisplayName("Метод saveCv выбрасывает FileProcessingException, если файл пустой")
    void shouldThrowFileProcessingExceptionForEmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty-cv.pdf", "application/pdf", new byte[0]);

        FileProcessingException exception = Assertions.assertThrows(FileProcessingException.class, () -> cvService.saveOrReplaceCv(emptyFile));

        assertEquals("Файл резюме пустой", exception.getMessage());
        verifyNoInteractions(cvRepository);
    }

    @Test
    @DisplayName("Успешное получение активных резюме")
    void shouldGetActiveMentorCVListSuccessfully() {
        Pageable pageable = PageRequest.of(0, 10);

        CV cv1 = new CV(UUID.randomUUID(), "CV 1", "pdf", null, false, null,
                UUID.randomUUID(), LocalDateTime.now());
        Page<CV> cvs = new PageImpl<>(List.of(cv1), pageable, 1);

        MentorCVResponse response1 = new MentorCVResponse(cv1.getId(), cv1.getUserId(), cv1.getTitle(),
                cv1.getExtension(), cv1.getIsArchived(), cv1.getArchivedDate(),
                cv1.getCreatedBy(), cv1.getCreated());

        when(cvRepository.findAllByIsArchived(false, pageable)).thenReturn(cvs);
        when(cvMapper.toMentorCVResponse(cv1)).thenReturn(response1);

        Page<MentorCVResponse> response = cvService.getMentorCVList(false, pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("CV 1", response.getContent().get(0).getTitle());


        verify(cvRepository, times(1)).findAllByIsArchived(false, pageable);
        verify(cvMapper, times(1)).toMentorCVResponse(cv1);
    }

    @Test
    @DisplayName("Успешное получение всех резюме (архивные + активные)")
    void shouldGetAllMentorCVListSuccessfully() {
        Pageable pageable = PageUtils.pageable(0, 10, "created");

        CV cv1 = new CV(UUID.randomUUID(), "CV 1", "pdf", null, false, null,
                UUID.randomUUID(), LocalDateTime.now());
        CV cv2 = new CV(UUID.randomUUID(), "CV 2", "pdf", null, true, LocalDateTime.now(),
                UUID.randomUUID(), LocalDateTime.now());
        cv1.setId(UUID.fromString("245ff50b-c392-44f9-a36d-9bd8ce321256"));
        cv2.setId(UUID.fromString("4cdc84fe-a645-4c9e-a7bc-fd44b9a2c3d6"));
        Page<CV> cvs = new PageImpl<>(List.of(cv1, cv2), pageable, 2);

        MentorCVResponse response1 = new MentorCVResponse(cv1.getId(), cv1.getUserId(), cv1.getTitle(),
                cv1.getExtension(), cv1.getIsArchived(), cv1.getArchivedDate(),
                cv1.getCreatedBy(), cv1.getCreated());
        MentorCVResponse response2 = new MentorCVResponse(cv2.getId(), cv2.getUserId(), cv2.getTitle(),
                cv2.getExtension(), cv2.getIsArchived(), cv2.getArchivedDate(),
                cv2.getCreatedBy(), cv2.getCreated());

        when(cvRepository.findAll(pageable)).thenReturn(cvs);
        when(cvMapper.toMentorCVResponse(cv1)).thenReturn(response1);
        when(cvMapper.toMentorCVResponse(cv2)).thenReturn(response2);

        Page<MentorCVResponse> result = cvService.getMentorCVList(true, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("CV 1", result.getContent().get(0).getTitle());
        assertEquals("CV 2", result.getContent().get(1).getTitle());

        verify(cvRepository, times(1)).findAll(pageable);
        verify(cvMapper, times(1)).toMentorCVResponse(cv1);
        verify(cvMapper, times(1)).toMentorCVResponse(cv2);
    }

    @Test
    @DisplayName("Пустой результат, если резюме не найдено")
    void shouldReturnEmptyListWhenNoCVsFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(cvRepository.findAllByIsArchived(false, pageable)).thenReturn(Page.empty());

        Page<MentorCVResponse> response = cvService.getMentorCVList(false, pageable);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());

        verify(cvRepository, times(1)).findAllByIsArchived(false, pageable);
        verifyNoInteractions(cvMapper);
    }

    @Test
    @DisplayName("Успешное удаление люблюго резюме по ID")
    void shouldDeleteCVSuccessfully() {
        CV cv = new CV();
        cv.setId(Id);
        cv.setUserId(UUID.randomUUID());
        cv.setTitle("Test CV");
        cv.setIsArchived(false);

        MentorCVResponse expectedResponse = new MentorCVResponse();
        expectedResponse.setId(cv.getId());
        expectedResponse.setArchived(true);

        when(cvRepository.findById(Id)).thenReturn(Optional.of(cv));
        when(cvRepository.save(cv)).thenReturn(cv);
        when(cvMapper.toMentorCVResponse(cv)).thenReturn(expectedResponse);

        MentorCVResponse actualResponse = cvService.deleteCVByIdWithRoleModerator(Id);

        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertTrue(actualResponse.isArchived());
        verify(cvRepository, times(1)).findById(Id);
        verify(cvRepository, times(1)).save(cv);
        verify(cvMapper, times(1)).toMentorCVResponse(cv);

        assertTrue(cv.getIsArchived());
        assertNotNull(cv.getArchivedDate());
    }

    @Test
    @DisplayName("Метод deleteCVById выбрасывает NotFoundException, если резюме не найдено")
    void shouldThrowNotFoundExceptionWhenDeletingNonexistentCV() {
        when(cvRepository.findById(Id)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> cvService.deleteCVByIdWithRoleModerator(Id));

        assertEquals("CV not found for id: " + Id, exception.getMessage());
        verify(cvRepository, times(1)).findById(Id);
        verifyNoInteractions(cvMapper);
    }

    @Test
    @DisplayName("Метод deleteCvByID успешно возвращает резюме и проставляются флаги isArchived и archiveDate")
    void shouldDeleteCvByIdSuccessfully() {
        CV cv = new CV();
        cv.setId(Id);
        cv.setUserId(Id);

        CVResponse cvResponse = new CVResponse();

        when(cvRepository.findById(cv.getId())).thenReturn(Optional.of(cv));
        when(securityUtil.getCurrentUserId()).thenReturn(Id);
        when(cvRepository.save(any(CV.class))).thenReturn(cv);
        when(cvMapper.toCVResponse(cv)).thenReturn(cvResponse);

        CVResponse result = cvService.deleteCvByID(cv.getId());

        assertNotNull(result);
        assertEquals(cvResponse, result);
        assertTrue(cv.getIsArchived());
        assertNotNull(cv.getArchivedDate());

        verify(cvRepository, times(1)).findById(Id);
        verify(securityUtil, times(1)).getCurrentUserId();
        verify(cvRepository, times(1)).save(cv);
        verify(cvMapper, times(1)).toCVResponse(cv);
    }

    @Test
    @DisplayName("Метод deleteCvByID выбрасывает исключение NotFoundException, если CV не найдено")
    void shouldThrowNotFoundExceptionWhenCvNotFound() {
        when(cvRepository.findById(Id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> cvService.deleteCvByID(Id));

        assertEquals("Резюме не найдено", exception.getMessage());

        verify(cvRepository, times(1)).findById(Id);
        verify(securityUtil, never()).getCurrentUserId();
        verify(cvRepository, never()).save(any(CV.class));
    }

    @Test
    @DisplayName("Метод deleteCvByID выбрасывает исключение InvalidIDException при попытке удалить чужое резюме")
    void shouldThrowInvalidIdExceptionWhenUserTriesToDeleteCVWithInvalidUserId() {
        CV cv = new CV();
        cv.setId(Id);
        UUID correctId = UUID.randomUUID();
        UUID invalidId = UUID.randomUUID();
        cv.setUserId(correctId);

        when(cvRepository.findById(Id)).thenReturn(Optional.of(cv));
        when(securityUtil.getCurrentUserId()).thenReturn(invalidId);

        InvalidIDException exception = assertThrows(InvalidIDException.class, () -> cvService.deleteCvByID(cv.getId()));

        assertEquals("Нельзя удалить чужое резюме", exception.getMessage());

        verify(cvRepository, times(1)).findById(Id);
        verify(securityUtil, times(1)).getCurrentUserId();
        verify(cvRepository, never()).save(any(CV.class));
    }

    @Test
    @DisplayName("Проверяет что метод deleteAllArchivedAndExpiredUsers корректно вызывает метод репозитория с переданной датой")
    void shouldDeleteAllArchivedAndExpiredUsersSuccessfully() {
        LocalDateTime dateTime = LocalDateTime.now();

        cvService.deleteAllArchivedAndExpiredCVs(dateTime);

        verify(cvRepository).deleteAllByIsArchivedTrueAndArchivedDateBefore(dateTime);
    }
}