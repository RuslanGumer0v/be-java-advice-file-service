package com.itm.advice.fileservice.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {super(message);}

    public FileProcessingException(String message, Throwable cause) {super(message, cause);}
}
