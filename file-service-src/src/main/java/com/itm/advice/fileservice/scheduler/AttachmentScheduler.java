package com.itm.advice.fileservice.scheduler;

import com.itm.advice.fileservice.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AttachmentScheduler {

    private static final int OFFSET = 30;

    private final AttachmentRepository attachmentRepository;

    @Scheduled(cron = "${attachment.cleanup.cron}")
    public void cleanupArchived() {
        var dateTime = LocalDateTime.now().minusDays(OFFSET);
        attachmentRepository.deleteAll(
                attachmentRepository.findAllByIsArchivedTrueAndArchivedDateBefore(dateTime)
        );
    }
}