package com.itm.advice.fileservice.exception;

public class InvalidIDException extends RuntimeException {
    public InvalidIDException(String message) {
        super(message);
    }
}
