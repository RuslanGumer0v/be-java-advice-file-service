package com.itm.advice.fileservice.exception;

public class AttachmentNotFoundException extends RuntimeException {
  public AttachmentNotFoundException(String message) {
    super(message);
  }
}
