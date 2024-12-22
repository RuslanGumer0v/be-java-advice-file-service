package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.CV;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CVApplicationTest extends BaseIntegrationTest {

    @Autowired
    private CVRepository cvRepository;

    UUID userId = UUID.fromString("c0f4bd7c-3b76-4a03-80e9-b8c6f0aa7dc2");
    UUID createdBy = UUID.fromString("dc533cff-e0bb-43ba-836a-c22f3a94ca6c");

    @DisplayName("Создание и получение модели с проверкой поля ID")
    @Test
    void shouldCreateAndGenerateId() {
        CV cv = new CV();
        cv.setUserId(userId);
        cv.setTitle("Test_CV");
        cv.setExtension("gg");
        cv.setIsArchived(false);
        cv.setCreatedBy(createdBy);

        cvRepository.save(cv);

        assertThat(cv.getId()).isNotNull();
        assertThat(cv.getCreated()).isNotNull();

        Optional<CV> savedCVOptional = cvRepository.findById(cv.getId());

        assertThat(savedCVOptional.get()).isEqualTo(cv);
    }

    @DisplayName("Обновление модели")
    @Test
    void shouldUpdateCV() {
        CV cv = new CV();
        cv.setId(userId);
        cv.setTitle("Test_CV");
        cv.setExtension("gg");
        cv.setIsArchived(false);
        cv.setCreatedBy(createdBy);

        cvRepository.save(cv);
        String title = cv.getTitle();
        cv.setTitle("Updated_Attachment");
        cvRepository.save(cv);

        Optional<CV> savedCVOptional = cvRepository.findById(cv.getId());

        assertThat(savedCVOptional.get()).isEqualTo(cv);
        assertThat(savedCVOptional.get().getTitle()).isNotEqualTo(title);
    }
}
