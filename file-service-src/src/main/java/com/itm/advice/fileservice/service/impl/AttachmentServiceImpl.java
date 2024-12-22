package com.itm.advice.fileservice.service.impl;

import com.itm.advice.fileservice.domain.entity.Attachment;
import com.itm.advice.fileservice.exception.AttachmentNotFoundException;
import com.itm.advice.fileservice.exception.FileProcessingException;
import com.itm.advice.fileservice.repository.AttachmentRepository;
import com.itm.advice.fileservice.service.AttachmentService;
import com.itm.advice.fileservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public UUID saveAttachment(MultipartFile file) {
        Attachment attachment = new Attachment();
        try {
            attachment.setFile(new Binary(file.getBytes()));
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка при обработке файла", e);
        }
        attachment.setUserId(securityUtil.getCurrentUserId());
        attachment.setTitle(file.getOriginalFilename());
        attachment.setIsArchived(false);
        attachment.setCreatedBy(securityUtil.getCurrentUserId());
        attachmentRepository.save(attachment);
        return attachment.getId();
    }

    @Override
    @Transactional
    public void deleteAttachmentById(UUID attachmentId, UUID userId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("Attachment with ID " + attachmentId + " not found"));
        if (!attachment.getUserId().equals(userId)) {
            throw new SecurityException("Нет прав для удаления этого файла");
        }
        attachmentRepository.deleteById(attachmentId);
    }
}

