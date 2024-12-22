package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.response.CVUploadResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.ADMIN_CV_LIST;
import static com.itm.advice.fileservice.constants.ApiConstant.CV_URL;
import static com.itm.advice.fileservice.constants.RoleConstant.ADMIN;
import static com.itm.advice.fileservice.constants.RoleConstant.MODERATOR;
import static com.itm.advice.fileservice.constants.RoleConstant.USER;

@Tag(name = "CV Controller", description = "Получение резюме")
public interface CVController {

    @Operation(summary = "Get CV file", description = "Получить CV файл по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный ответ с pdf файлом"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Не достаточно прав доступа"),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(CV_URL + "/{id}")
    @Secured({ADMIN, MODERATOR})
    ResponseEntity<ByteArrayResource> getCV(@PathVariable UUID id);

    @Operation(summary = "Upload CV", description = "Загрузить резюме для пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно загружен файл"),
            @ApiResponse(responseCode = "400", description = "Плохой запрос (например, пустой файл)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Не достаточно прав доступа"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping(value = CV_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({"ROLE_USER"})
    CVUploadResponse uploadFile(@RequestParam("file") MultipartFile file);

    @Operation(summary = "Get CVs of mentor candidates",
            description = "Возвращает список резюме кандидатов в менторы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный запрос, возвращен список резюме"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "У пользователя нет прав для выполнения запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(ADMIN_CV_LIST)
    @Secured({ADMIN, MODERATOR})
    ResponseEntity<List<MentorCVResponse>> getMentorCVList(
            @RequestParam(defaultValue = "false") boolean archived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Delete CV", description = "Удалить собственное резюме пользователя по UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешно удалено резюме"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав пользователя"),
            @ApiResponse(responseCode = "404", description = "Резюме не найдено"),
            @ApiResponse(responseCode = "409", description = "Нельзя удалить чужое резюме"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(CV_URL + "/{id}")
    @Secured({USER})
    ResponseEntity<ByteArrayResource> deleteCV(@PathVariable UUID id);
}