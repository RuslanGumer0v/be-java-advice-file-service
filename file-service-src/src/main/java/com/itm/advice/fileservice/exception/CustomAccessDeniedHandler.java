package com.itm.advice.fileservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.advice.fileservice.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ErrorResponse errorResponse = new ErrorResponse(
                403,
                "FORBIDDEN",
                accessDeniedException.getMessage()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}

