package com.itm.advice.fileservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AttachmentService {

    UUID saveAttachment(MultipartFile file);

    void deleteAttachmentById(UUID attachmentId, UUID userId);
}
