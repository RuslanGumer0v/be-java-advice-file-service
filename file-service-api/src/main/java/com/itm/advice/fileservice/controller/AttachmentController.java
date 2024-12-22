package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.response.AttachmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static com.itm.advice.fileservice.constants.ApiConstant.ATTACHMENTS_PATH;

@RequestMapping(ATTACHMENTS_PATH)
@Tag(name = "Attachment Controller", description = "Управление вложениями")
public interface AttachmentController {

    @Operation(summary = "Upload an attachment", description = "Загрузка вложения")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ответ с загруженным файлом"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })

    @PostMapping
    AttachmentResponse uploadFile(@RequestParam("file") MultipartFile file);

    @Operation(summary = "Delete an attachment", description = "Удаление вложения по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно удалён"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "404", description = "Файл не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })

    @DeleteMapping("/{id}")
    void deleteAttachment(@PathVariable("id") String id);
}


