package com.itm.advice.fileservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.advice.fileservice.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse errorResponse = new ErrorResponse(
                401,
                "Unauthorized",
                authException.getMessage()
        );
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
