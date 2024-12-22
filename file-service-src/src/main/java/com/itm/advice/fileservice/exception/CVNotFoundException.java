package com.itm.advice.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CVNotFoundException extends RuntimeException {
    public CVNotFoundException(UUID id) {
        super("Резюме с ID " + id + " не найдено.");
    }
}
