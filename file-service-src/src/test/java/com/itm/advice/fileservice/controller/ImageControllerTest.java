package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.constants.ApiConstant;
import com.itm.advice.fileservice.domain.entity.Image;
import com.itm.advice.fileservice.repository.ImageRepository;
import lombok.SneakyThrows;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.IMAGES_URL;
import static com.itm.advice.fileservice.constants.RoleConstant.ADMIN;
import static com.itm.advice.fileservice.constants.RoleConstant.ADVISER;
import static com.itm.advice.fileservice.constants.RoleConstant.MODERATOR;
import static com.itm.advice.fileservice.constants.RoleConstant.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageControllerTest extends BaseIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    private final UUID randomUUID = UUID.randomUUID();

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN, USER, ADVISER, MODERATOR})
    @DisplayName("Получаем jpeg файл, ответ 200")
    void shouldGetImageById() {

        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("img.jpeg");
        Objects.requireNonNull(resourceStream, "Файл img.jpeg отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        Image image = new Image();
        image.setUserId(randomUUID);
        image.setTitle("Test_Image");
        image.setExtension("jpeg");
        image.setFile(new Binary(data));
        image.setIsArchived(false);
        image.setCreatedBy(randomUUID);
        image.setArchivedDate(null);
        image.setCreated(null);

        imageRepository.save(image);

        assertThat(image.getId()).isNotNull();

        MockHttpServletResponse response = mockMvc.perform(get(ApiConstant.IMAGES_URL + "/{id}", image.getId())
                        .with(csrf()))
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andDo(print())
                .andReturn()
                .getResponse();

        assertThat(response.getContentAsByteArray()).isEqualTo(data);
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN, USER, ADVISER, MODERATOR})
    @DisplayName("Изображение не найдено, ответ 404")
    void GetImageByWrongId() {
        mockMvc.perform(get(ApiConstant.IMAGES_URL + "/{id}", randomUUID)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @DisplayName("Пытаемся получить не авторизовавшись, ошибка 401")
    void getImageByIdAsUnauthorized() {
        mockMvc.perform(get(ApiConstant.IMAGES_URL + "/{id}", randomUUID)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @SneakyThrows
    @WithMockUser(authorities = {ADMIN, USER, ADVISER, MODERATOR})
    @DisplayName("Изображение находится в архиве, ответ 404")
    void GetImageByIdWhereIsArchivedTrue() {

        InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("img.jpeg");
        Objects.requireNonNull(resourceStream, "Файл img.jpeg отсутствует в ресурсах");

        byte[] data;
        try (BufferedInputStream bis = new BufferedInputStream(resourceStream)) {
            data = bis.readAllBytes();
        }

        Image image = new Image();
        image.setUserId(randomUUID);
        image.setTitle("Test_Image");
        image.setExtension("jpeg");
        image.setFile(new Binary(data));
        image.setIsArchived(true);
        image.setCreatedBy(randomUUID);
        image.setArchivedDate(null);
        image.setCreated(null);

        imageRepository.save(image);

        assertThat(image.getId()).isNotNull();

        mockMvc.perform(get(ApiConstant.IMAGES_URL + "/{id}", image.getId())
                        .with(csrf()))
                .andExpect(status().isNotFound()) // Ожидаем статус 404, так как изображение архивное
                .andDo(print())
                .andReturn()
                .getResponse();
    }

    @Test
    @DisplayName("Успешная архивация фотографии пользователя - 200")
    @WithMockUser(roles = {"ADMIN"}, username = "123e4567-e89b-12d3-a456-426614174001")
    void shouldSuccessfullyArchiveImage() throws Exception {
        Image expectedImage = jsonParserUtil.getObjectFromJson("/json/image.json", Image.class);
        expectedImage.setFile(new Binary(new byte[]{1, 2, 3}));
        imageRepository.save(expectedImage);

        mockMvc.perform(delete(IMAGES_URL + "/{id}", expectedImage.getId())
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Пользователь не аутентифицирован для архивации изображения профиля - 401")
    void archiveImageUserNotAuthenticated() throws Exception {
        mockMvc.perform(delete(IMAGES_URL + UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Архивировать можно только свою фотографию - 403")
    @WithMockUser(roles = {"ADMIN"}, username = "123e4567-e89b-12d3-a456-426614174005")
    void imageIsForbidden() throws Exception {
        Image expectedImage = jsonParserUtil.getObjectFromJson("/json/image.json", Image.class);
        expectedImage.setFile(new Binary(new byte[]{1, 2, 3}));
        imageRepository.save(expectedImage);

        mockMvc.perform(delete(IMAGES_URL + "/{id}", expectedImage.getId())
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Архивируемое изображение не найдено - 404")
    @WithMockUser(roles = {"ADMIN"}, username = "123e4567-e89b-12d3-a456-426614174001")
    void archivedImageIsNotFound() throws Exception {
        mockMvc.perform(delete(IMAGES_URL + UUID.randomUUID())
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
