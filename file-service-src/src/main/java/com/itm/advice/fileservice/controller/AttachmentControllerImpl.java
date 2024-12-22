package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.response.AttachmentResponse;
import com.itm.advice.fileservice.service.AttachmentService;
import com.itm.advice.fileservice.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttachmentControllerImpl implements AttachmentController {

    private final AttachmentService attachmentService;
    private final SecurityUtil securityUtil;

    @Override
    public AttachmentResponse uploadFile(@RequestParam("file") MultipartFile file) {

        UUID fileId = attachmentService.saveAttachment(file);
        return new AttachmentResponse(fileId);
    }

    @Override
    public void deleteAttachment(String id) {
        UUID fileId = UUID.fromString(id);
        UUID currentUserId = securityUtil.getCurrentUserId();
        attachmentService.deleteAttachmentById(fileId, currentUserId);
    }
}
