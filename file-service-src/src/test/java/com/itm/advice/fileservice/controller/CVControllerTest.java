package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.constants.ApiConstant;
import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.exception.NotFoundException;
import com.itm.advice.fileservice.repository.CVRepository;
import lombok.SneakyThrows;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.itm.advice.fileservice.constants.RoleConstant.ADMIN;
import static com.itm.advice.fileservice.constants.RoleConstant.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CVControllerTest extends BaseIntegrationTest {

    @Autowired
    private CVRepository cvRepository;

    private final UUID randomUUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        cvRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN})
    @DisplayName("Получаем pdf файл, ответ 200")
    void getCVByIdAsAdmin() {

        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("cv-file.pdf");
        Objects.requireNonNull(resourceStream, "Файл cv-file.pdf отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        CV cv = new CV();
        cv.setUserId(randomUUID);
        cv.setTitle("Test_CV");
        cv.setExtension("cv");
        cv.setFile(new Binary(data));
        cv.setIsArchived(false);
        cv.setCreatedBy(randomUUID);

        cvRepository.save(cv);

        assertThat(cv.getId()).isNotNull();

        MockHttpServletResponse response = mockMvc.perform(get(ApiConstant.CV_URL + "/{id}", cv.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isEqualTo(data);
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN})
    @DisplayName("Пытаемся получить по несуществующему id, ошибка 404")
    void getCVByWrongIdAsAdmin() {
        mockMvc.perform(get(ApiConstant.CV_URL + "/{id}", randomUUID)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {USER})
    @DisplayName("Пытаемся получить с недостаточными правами, ошибка 403")
    void getCVByIdAsUser() {
        mockMvc.perform(get(ApiConstant.CV_URL + "/{id}", randomUUID)
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("Пытаемся получить не авторизовавшись, ошибка 401")
    void getCVByIdAsUnauthorized() {
        mockMvc.perform(get(ApiConstant.CV_URL + "/{id}", randomUUID)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000", authorities = {USER})
    @DisplayName("Загрузка резюме пользователем с ролью ROLE_USER, ответ 200")
    void shouldUploadUserCVSuccessfully() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-cv.pdf",
                "application/pdf",
                "This is a test CV".getBytes()
        );

        MockHttpServletResponse response = mockMvc.perform(
                        multipart(ApiConstant.CV_URL)
                                .file(mockFile)
                                .with(csrf())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse();

        String jsonResponse = response.getContentAsString();
        assertThat(jsonResponse).contains("id");
    }

    @Test
    @SneakyThrows
    @DisplayName("Пытаемся загрузить резюме без авторизации, ошибка 401")
    void shouldReturnUnauthorizedForUploadWithoutAuth() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-cv.pdf",
                "application/pdf",
                "This is a test CV".getBytes()
        );

        mockMvc.perform(
                        multipart(ApiConstant.CV_URL)
                                .file(mockFile)
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {"ROLE_GUEST"})
    @DisplayName("Пользователь без роли ROLE_USER не имеет доступа, ошибка 403")
    void shouldReturnForbiddenForNonUserRole() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-cv.pdf",
                "application/pdf",
                "This is a test CV".getBytes()
        );

        mockMvc.perform(
                        multipart(ApiConstant.CV_URL)
                                .file(mockFile)
                                .with(csrf())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000", authorities = {USER})
    @DisplayName("Пытаемся загрузить файл с неверным форматом, ошибка 400")
    void shouldReturnBadRequestForInvalidFileFormat() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-cv.txt",
                "text/plain",
                "Invalid CV format".getBytes()
        );

        mockMvc.perform(
                        multipart(ApiConstant.CV_URL)
                                .file(mockFile)
                                .with(csrf())
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN})
    @DisplayName("Получение списка резюме с ролью ADMIN, ответ 200")
    void getMentorCVListAsAdmin() {

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime archivedDate = now.minusDays(1);

        cvRepository.saveAll(List.of(
                new CV(userId1, "CV 1", "pdf", new Binary("Test CV 1".getBytes()), false,
                        null, UUID.randomUUID(), null),
                new CV(userId2, "CV 2", "docx", new Binary("Test CV 2".getBytes()), true,
                        archivedDate, UUID.randomUUID(), null)
        ));

        MockHttpServletResponse response = mockMvc.perform(
                        get(ApiConstant.ADMIN_CV_LIST)
                                .param("archived", "false")
                                .param("page", "0")
                                .param("size", "10")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse();

        String jsonResponse = response.getContentAsString();
        assertThat(jsonResponse).contains("CV 1");
        assertThat(jsonResponse).doesNotContain("CV 2");
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {USER})
    @DisplayName("Получение списка резюме с ролью USER, ошибка 403")
    void getMentorCVListAsUser() {
        mockMvc.perform(
                        get(ApiConstant.ADMIN_CV_LIST)
                                .param("archived", "false")
                                .param("page", "0")
                                .param("size", "10")
                                .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("Получение списка резюме без авторизации, ошибка 401")
    void getMentorCVListWithoutAuth() {
        mockMvc.perform(
                        get(ApiConstant.ADMIN_CV_LIST)
                                .param("archived", "false")
                                .param("page", "0")
                                .param("size", "10")
                                .with(csrf())
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000", authorities = {USER})
    @DisplayName("Удаляем резюме путем проставления флагов isArchived и archiveDate, возвращаем резюме, ответ 200")
    void shouldDeleteCVByChangingFlagsAndReturnFlagsSuccessfully() {

        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("cv-file.pdf");
        Objects.requireNonNull(resourceStream, "Файл cv-file.pdf отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        UUID userID = UUID.fromString("c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000");

        CV cv = new CV();
        cv.setUserId(userID);
        cv.setTitle("Test_CV");
        cv.setExtension("cv");
        cv.setFile(new Binary(data));
        cv.setIsArchived(false);
        cv.setCreatedBy(randomUUID);

        cvRepository.save(cv);

        assertThat(cv.getId()).isNotNull();

        MockHttpServletResponse response = mockMvc.perform(delete(ApiConstant.CV_URL + "/{id}", cv.getId())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isEqualTo(data);

        CV deletedCV = cvRepository.findById(cv.getId()).orElseThrow(() -> new NotFoundException("Резюме не найдено"));
        assertThat(deletedCV.getIsArchived()).isTrue();
        assertThat(deletedCV.getArchivedDate()).isNotNull();
    }

    @Test
    @SneakyThrows
    @DisplayName("Пытаемся удалить резюме, получаем ошибку 401")
    void shouldReturnUnauthorisedForUnauthorized() {
        mockMvc.perform(delete(ApiConstant.CV_URL + "/{id}", randomUUID)
                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {"ROLE_GUEST"})
    @DisplayName("Пытаемся удалить резюме, недостаточно прав пользователя, получаем ошибку 403")
    void shouldReturnForbiddenForGuestUserRole() {

        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("cv-file.pdf");
        Objects.requireNonNull(resourceStream, "Файл cv-file.pdf отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        UUID userID = UUID.fromString("c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000");

        CV cv = new CV();
        cv.setUserId(userID);
        cv.setTitle("Test_CV");
        cv.setExtension("cv");
        cv.setFile(new Binary(data));
        cv.setIsArchived(false);
        cv.setCreatedBy(randomUUID);

        cvRepository.save(cv);

        mockMvc.perform(delete(ApiConstant.CV_URL + "/{id}", cv.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000", authorities = {USER})
    @DisplayName("Резюме для удаления не найдено, получаем ошибку 404")
    void shouldReturnNotFoundForNotFound() {
        mockMvc.perform(delete(ApiConstant.CV_URL + "/{id}", randomUUID)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000", authorities = {USER})
    @DisplayName("Пытаемся удалить чужое резюме, получаем ошибку 409")
    void shouldReturnConflictForWrongUserId() {
        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("cv-file.pdf");
        Objects.requireNonNull(resourceStream, "Файл cv-file.pdf отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        UUID wrongUserID = UUID.randomUUID();

        CV cv = new CV();
        cv.setUserId(wrongUserID);
        cv.setTitle("Test_CV");
        cv.setExtension("cv");
        cv.setFile(new Binary(data));
        cv.setIsArchived(false);
        cv.setCreatedBy(randomUUID);

        cvRepository.save(cv);

        mockMvc.perform(delete(ApiConstant.CV_URL + "/{id}", cv.getId())
                        .with(csrf()))
                .andExpect(status().isConflict())
                .andDo(print());
    }
}
