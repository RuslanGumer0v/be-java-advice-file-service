package com.itm.advice.fileservice.controller;


import com.itm.advice.fileservice.constants.RoleConstant;
import com.itm.advice.fileservice.response.MentorCVResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static com.itm.advice.fileservice.constants.ApiConstant.ADMIN_URL;
import static com.itm.advice.fileservice.constants.ApiConstant.CV_URL;

@RequestMapping(ADMIN_URL + CV_URL)
public interface ModeratorCVController {
    @Secured({RoleConstant.MODERATOR, RoleConstant.ADMIN})
    @Operation(
            summary = "Delete any candidate's mentor resume",
            description = "Удалить любое резюме кандидата в менторы"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Резюме успешно удалена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав пользователя"),
            @ApiResponse(responseCode = "404", description = "Резюме не найдено"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/{id}")
    MentorCVResponse deleteCV(
            @Parameter(description = "Идентификатор резюме", required = true)
            @PathVariable("id") UUID id
    );
}