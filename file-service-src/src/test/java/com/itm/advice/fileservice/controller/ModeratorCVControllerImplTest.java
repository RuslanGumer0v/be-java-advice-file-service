package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.repository.CVRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.ADMIN_URL;
import static com.itm.advice.fileservice.constants.ApiConstant.CV_URL;
import static com.itm.advice.fileservice.constants.RoleConstant.ADMIN;
import static com.itm.advice.fileservice.constants.RoleConstant.MODERATOR;
import static com.itm.advice.fileservice.constants.RoleConstant.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ModeratorCVControllerImplTest extends BaseIntegrationTest {

    @Autowired
    private CVRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        CV cv = new CV();
        cv.setUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        cv.setCreatedBy(UUID.fromString("2c4a230c-5085-4924-a3e1-25fb4fc5965b"));
        cv.setId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        cv.setIsArchived(false);
        cv.setArchivedDate(null);
        repository.save(cv);
    }

    @Test
    @WithMockUser(username = "2c4a230c-5085-4924-a3e1-25fb4fc5965b", authorities = {ADMIN, MODERATOR})
    @DisplayName("200: Резюме успешно удалено")
    void shouldDeleteCVSuccess() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        delete(ADMIN_URL + CV_URL + "/123e4567-e89b-12d3-a456-426614174000")
                                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(jsonPath("$.isArchived").value(true))
                .andExpect(jsonPath("$.archiveDate").exists())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse();

        CV cv = repository.findByUserId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")).orElseThrow();
        System.out.println(cv.getIsArchived());
        System.out.println(cv.getArchivedDate());
        assertThat(cv.getIsArchived()).isEqualTo(true);
        assertThat(cv.getArchivedDate()).isNotNull();
    }

    @SneakyThrows
    @Test
    @DisplayName("401: Пользователь не аутентифицирован")
    void shouldNotDeleteCVUserNotAuth() {
        mockMvc.perform(delete(ADMIN_URL + CV_URL + "/123e4567-e89b-12d3-a456-426614174000")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @SneakyThrows
    @Test
    @DisplayName("403: Недостаточно прав пользователя")
    @WithMockUser(authorities = {USER})
    void shouldNotDeleteCVUserWithRoleUser() {
        mockMvc.perform(
                        delete(ADMIN_URL + CV_URL + "/123e4567-e89b-12d3-a456-426614174000")
                                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "2c4a230c-5085-4924-a3e1-25fb4fc5965b", authorities = {ADMIN, MODERATOR})
    @DisplayName("404: Резюме не найденно")
    void shouldThrowNotFoundExeptionWithIncorrectID() throws Exception {
        mockMvc.perform(
                        delete(ADMIN_URL + CV_URL + "/123e4567-e89b-12d3-a456-426688888888")
                                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}