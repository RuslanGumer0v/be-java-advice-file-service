package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.Image;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyImageRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @DisplayName("Возвращает архивированные записи старше 30 дней")
    @Description("Должен получить список Image, у которых флаг isArchived = true, и дата архивации больше текущей даты-времени более чем на 30 дней")
    void findOldArchivedImages() {

        Image expiredImage = new Image();
        expiredImage.setIsArchived(true);
        expiredImage.setArchivedDate(LocalDateTime.now().minusDays(200));
        Image notExpiredImage = new Image();
        notExpiredImage.setIsArchived(true);
        notExpiredImage.setArchivedDate(LocalDateTime.now().minusDays(20));

        Image savedExpiredImage = imageRepository.save(expiredImage);
        Image savedNotExpiredImage = imageRepository.save(notExpiredImage);

        List<Image> actual = imageRepository.findAllByIsArchivedTrueAndArchivedDateBefore(LocalDateTime.now().minusDays(30));

        assertEquals(1, actual.size(), "Должен быть только один старый архивированный Image");
        assertTrue(actual.contains(savedExpiredImage), "Должен содержать старый архивированный Image");
        assertFalse(actual.contains(savedNotExpiredImage), "Не должен содержать новый архивированный Image");
    }
}
