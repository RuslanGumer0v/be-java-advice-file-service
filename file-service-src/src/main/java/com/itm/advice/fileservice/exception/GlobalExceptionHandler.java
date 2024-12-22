package com.itm.advice.fileservice.exception;

import com.github.dockerjava.api.exception.BadRequestException;
import com.itm.advice.fileservice.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCVNotFoundException(NotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }


    @ExceptionHandler(InvalidIDException.class)
    public ResponseEntity<ErrorResponse> handleCVInvalidIDException(InvalidIDException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(HttpServerErrorException.InternalServerError ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleFileProcessingException(FileProcessingException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<String> handleInvalidFileFormat(InvalidFileFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AttachmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAttachmentNotFoundException(AttachmentNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ATTACHMENT_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDeniedException(PermissionDeniedException permissionDeniedException) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                "FORBIDDEN",
                permissionDeniedException.getMessage()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
