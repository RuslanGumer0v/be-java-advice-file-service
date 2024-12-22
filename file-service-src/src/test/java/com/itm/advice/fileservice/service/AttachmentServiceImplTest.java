package com.itm.advice.fileservice.service;

import com.itm.advice.fileservice.BaseUnitTest;
import com.itm.advice.fileservice.domain.entity.Attachment;
import com.itm.advice.fileservice.exception.AttachmentNotFoundException;
import com.itm.advice.fileservice.repository.AttachmentRepository;
import com.itm.advice.fileservice.service.impl.AttachmentServiceImpl;
import com.itm.advice.fileservice.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttachmentServiceImplTest extends BaseUnitTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Test
    void testSaveAttachment() throws IOException {

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());
        when(mockFile.getOriginalFilename()).thenReturn("testfile.txt");

        UUID userId = UUID.randomUUID();
        when(securityUtil.getCurrentUserId()).thenReturn(userId);

        Attachment mockAttachment = new Attachment();
        UUID expectedId = UUID.randomUUID();
        mockAttachment.setId(expectedId);
        mockAttachment.setUserId(userId);
        mockAttachment.setCreated(LocalDateTime.now());

        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> {
            Attachment savedAttachment = invocation.getArgument(0);
            savedAttachment.setId(expectedId);
            savedAttachment.setCreated(LocalDateTime.now());
            return savedAttachment;
        });

        UUID savedId = attachmentService.saveAttachment(mockFile);
        verify(attachmentRepository, times(1)).save(any(Attachment.class));

        assertNotNull(savedId, "Сохраненный ID не должен быть null");
        assertEquals(expectedId, savedId, "Сохраненный ID должен совпадать с ожидаемым ID");

        ArgumentCaptor<Attachment> captor = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentRepository).save(captor.capture());

        Attachment capturedAttachment = captor.getValue();
        assertEquals("testfile.txt", capturedAttachment.getTitle(), "Название файла должно совпадать");
        assertEquals(userId, capturedAttachment.getUserId(), "UserId должен совпадать");

        assertEquals("test content".getBytes().length, capturedAttachment.getFile().getData().length, "Содержимое файла должно совпадать");
        assertEquals(false, capturedAttachment.getIsArchived(), "Флаг архивирования должен быть false");
        assertNotNull(capturedAttachment.getCreatedBy(), "Создатель не должен быть null");
        assertNotNull(capturedAttachment.getCreated(), "Дата создания не должна быть null");
    }
    @Test
    void shouldDeleteAttachmentByIdSuccessfully() {
        UUID attachmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Attachment mockAttachment = new Attachment();
        mockAttachment.setId(attachmentId);
        mockAttachment.setUserId(userId);

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(mockAttachment));
        doNothing().when(attachmentRepository).deleteById(attachmentId);
        assertDoesNotThrow(() ->attachmentService.deleteAttachmentById(attachmentId, userId));

        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, times(1)).deleteById(attachmentId);
    }

    @Test
    void shouldThrowExceptionWhenAttachmentNotFound() {
        UUID attachmentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());
        AttachmentNotFoundException exception =
                assertThrows(AttachmentNotFoundException.class,
                        () -> attachmentService.deleteAttachmentById(attachmentId, userId));
        assertEquals("Attachment with ID " + attachmentId + " not found", exception.getMessage());

        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, never()).deleteById(any(UUID.class));
    }
}
