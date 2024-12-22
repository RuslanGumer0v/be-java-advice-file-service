package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.Image;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ImageApplicationTest extends BaseIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    UUID userId = UUID.fromString("c0f4bd7c-3b76-4a03-80e9-b8c6f0aa7dc2");

    UUID createdBy = UUID.fromString("dc533cff-e0bb-43ba-836a-c22f3a94ca6c");

    private Image testImage;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        testImage = new Image();
        testImage.setUserId(userId);
        testImage.setTitle("Test Image");
        testImage.setExtension("jpg");
        testImage.setCreatedBy(createdBy);
        testImage.setIsArchived(false);
        testImage.setCreated(null);
        testImage.setArchivedDate(null);
    }

    @DisplayName("Тест должен сохранять изображение")
    @Test
    void testShouldSaveImage() {
        Image image = imageRepository.save(testImage);

        assertNotNull(image.getId());
        assertEquals(testImage.getTitle(), image.getTitle());
        assertEquals(testImage.getExtension(), image.getExtension());
    }

    @DisplayName("Тест должен находить изображение")
    @Test
    void testShouldFindImageById() {
        Image image = imageRepository.save(testImage);

        Image foundImage = imageRepository.findById(image.getId()).orElse(null);

        assertNotNull(foundImage);
        assertEquals(image.getTitle(), foundImage.getTitle());
        assertEquals(image.getExtension(), foundImage.getExtension());
        assertEquals(image.getId(), foundImage.getId());
    }
}
