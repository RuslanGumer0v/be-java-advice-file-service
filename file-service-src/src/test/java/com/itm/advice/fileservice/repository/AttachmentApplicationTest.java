package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.Attachment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class AttachmentApplicationTest extends BaseIntegrationTest {

    @Autowired
    private AttachmentRepository attachmentRepository;

    UUID userId = UUID.fromString("c0f4bd7c-3b76-4a03-80e9-b8c6f0aa7dc2");
    UUID createdBy = UUID.fromString("dc533cff-e0bb-43ba-836a-c22f3a94ca6c");

    @DisplayName("Создание и получение сущности с проверкой поля ID")
    @Test
    void shouldCreateAndGenerateId() {

        Attachment newAttachment = new Attachment();
        newAttachment.setUserId(userId);
        newAttachment.setTitle("Test_Attachment");
        newAttachment.setExtension("png");
        newAttachment.setIsArchived(false);
        newAttachment.setCreatedBy(createdBy);
        newAttachment.setCreated(LocalDateTime.now());

        Attachment savedAttachment = attachmentRepository.save(newAttachment);

        assertThat(savedAttachment.getId()).isNotNull();
        assertThat(savedAttachment.getCreated()).isNotNull();

        Optional<Attachment> foundAttachment = attachmentRepository
                .findById(savedAttachment.getId());

        assertThat(foundAttachment.get()).isEqualTo(savedAttachment);
    }

    @DisplayName("Обновление сущности")
    @Test
    void shouldUpdate() {

        Attachment newAttachment = new Attachment();
        newAttachment.setUserId(userId);
        newAttachment.setTitle("Test_Attachment");
        newAttachment.setExtension("png");
        newAttachment.setIsArchived(false);
        newAttachment.setCreatedBy(createdBy);

        attachmentRepository.save(newAttachment);
        String title = newAttachment.getTitle();
        newAttachment.setTitle("example title");
        attachmentRepository.save(newAttachment);

        Optional<Attachment> foundAttachment = attachmentRepository
                .findById(newAttachment.getId());

        assertThat(foundAttachment.get()).isEqualTo(newAttachment);
        assertThat(foundAttachment.get().getTitle()).isNotEqualTo(title);
    }
}
