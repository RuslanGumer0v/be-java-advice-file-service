package com.itm.advice.fileservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.IMAGES_URL;
import static com.itm.advice.fileservice.constants.RoleConstant.ADMIN;
import static com.itm.advice.fileservice.constants.RoleConstant.ADVISER;
import static com.itm.advice.fileservice.constants.RoleConstant.MODERATOR;
import static com.itm.advice.fileservice.constants.RoleConstant.USER;

@RequestMapping(IMAGES_URL)
@Tag(name = "Image Controller", description = "Получение фотографии")
public interface ImageController {

    @Operation(summary = "Get image", description = "Получить фотографию по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный ответ с файлом изображения"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Изображение не найдено"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{id}")
    @Secured({ADMIN, MODERATOR, USER, ADVISER})
    ResponseEntity<ByteArrayResource> getImage(@PathVariable UUID id);

    @Operation(summary = "Archive user image", description = "Архивация фотографии пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Фотография успешно архивирована"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Архивировать можно только собственную фотографию"),
            @ApiResponse(responseCode = "404", description = "Фотография не найдена"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @Secured({ADMIN, MODERATOR, USER, ADVISER})
    ResponseEntity<Void> archiveImage(@PathVariable UUID id);
}
