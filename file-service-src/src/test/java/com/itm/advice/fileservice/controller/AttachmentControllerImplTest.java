package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.ATTACHMENTS_PATH;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AttachmentControllerImplTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Успешная загрузка файла для аутентифицированного пользователя с любой ролью")
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test content".getBytes()
        );

        mockMvc.perform(multipart(ATTACHMENTS_PATH)
                        .file(file)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("user@gmail.ru", "password"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Неудачная загрузка файла для неаутентифицированного пользователя")
    void uploadFile_unauthorized() throws Exception {
        mockMvc.perform(multipart(ATTACHMENTS_PATH)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Успешная загрузка файла для аутентифицированного пользователя с ролью STUDENT")
    void uploadFile_success_student() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test content".getBytes()
        );

        mockMvc.perform(multipart(ATTACHMENTS_PATH)
                        .file(file)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("student@student.ru", "passwd"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Успешное удаление файла")
    void deleteAttachment_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test content".getBytes()
        );

        String response = mockMvc.perform(multipart(ATTACHMENTS_PATH)
                        .file(file)
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("user@gmail.ru", "password"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String fileId = JsonPath.parse(response).read("$.id");

        mockMvc.perform(delete(ATTACHMENTS_PATH + "/" + fileId)
                        .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("user@gmail.ru", "password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Попытка удалить несуществующий файл")
    void deleteAttachment_fileNotFound() throws Exception {
        String fileId = UUID.randomUUID().toString();
        mockMvc.perform(delete(ATTACHMENTS_PATH + "/" + fileId)
                        .header(HttpHeaders.AUTHORIZATION, authUtil.getAuthorization("user@gmail.ru", "password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Попытка удалить файл без авторизации")
    void deleteAttachment_unauthorized() throws Exception {
        String fileId = UUID.randomUUID().toString();
        mockMvc.perform(delete(ATTACHMENTS_PATH + "/" + fileId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
