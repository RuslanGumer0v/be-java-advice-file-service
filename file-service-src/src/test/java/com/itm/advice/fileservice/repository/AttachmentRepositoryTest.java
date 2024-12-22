package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.Attachment;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.mongodb.assertions.Assertions.assertFalse;
import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AttachmentRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Test
    @Description("Должен получить список Attachment, у которых флаг isArchived = true, и дата архивации больше текущей даты-времени более чем на 30 дней")
    void findOldArchivedAttachments() {
        var expiredAttachment = new Attachment();
        expiredAttachment.setIsArchived(true);
        expiredAttachment.setArchivedDate(LocalDateTime.now().minusDays(200));
        var notExpiredAttachment = new Attachment();
        notExpiredAttachment.setIsArchived(true);
        notExpiredAttachment.setArchivedDate(LocalDateTime.now().minusDays(20));

        var savedExpiredAttachment = attachmentRepository.save(expiredAttachment);
        var savedNotExpiredAttachment = attachmentRepository.save(notExpiredAttachment);

        var actual = attachmentRepository.findAllByIsArchivedTrueAndArchivedDateBefore(LocalDateTime.now().minusDays(30));

        assertEquals(1, actual.size());
        assertTrue(actual.contains(savedExpiredAttachment));
        assertFalse(actual.contains(savedNotExpiredAttachment));
    }
}