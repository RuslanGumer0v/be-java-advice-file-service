package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.domain.entity.CV;
import org.bson.types.Binary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class CVRepositoryTest {

    @Autowired
    private CVRepository cvRepository;

    private final CV cv = new CV();

    @BeforeEach
    void setUp() {
        cv.setId(UUID.randomUUID());
        cv.setUserId(UUID.randomUUID());
        cv.setTitle("Test CV");
        cv.setFile(new Binary("some data".getBytes()));
        cv.setExtension("pdf");
        cv.setIsArchived(false);
        cv.setCreatedBy(UUID.randomUUID());
    }

    @Test
    @DisplayName("Сохранение и поиск по userId")
    void shouldSaveAndFindByUserId() {

        cvRepository.save(cv);

        Optional<CV> foundCV = cvRepository.findByUserId(cv.getUserId());

        assertThat(foundCV).isPresent();
        assertThat(foundCV.get().getUserId()).isEqualTo(cv.getUserId());
        assertThat(foundCV.get().getTitle()).isEqualTo("Test CV");
        assertThat(foundCV.get().getExtension()).isEqualTo("pdf");
        assertThat(foundCV.get().getIsArchived()).isFalse();
    }

    @Test
    @DisplayName("Поиск отсутствующего CV")
    void shouldReturnEmptyWhenUserIdNotFound() {
        Optional<CV> result = cvRepository.findByUserId(UUID.randomUUID());
        assertThat(result).isEmpty();
    }
}
