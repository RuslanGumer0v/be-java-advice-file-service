package com.itm.advice.fileservice.service;

import com.itm.advice.fileservice.BaseUnitTest;
import com.itm.advice.fileservice.domain.entity.Image;
import com.itm.advice.fileservice.exception.NotFoundException;
import com.itm.advice.fileservice.exception.PermissionDeniedException;
import com.itm.advice.fileservice.mapper.ImageMapper;
import com.itm.advice.fileservice.repository.ImageRepository;
import com.itm.advice.fileservice.response.ImageResponse;
import com.itm.advice.fileservice.service.impl.ImageServiceImpl;
import com.itm.advice.fileservice.util.SecurityUtil;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageServiceTest extends BaseUnitTest {

    @InjectMocks
    private ImageServiceImpl imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageMapper imageMapper;

    @Mock
    private SecurityUtil securityUtil;

    private final UUID Id = UUID.fromString("c0f4bd7c-3b76-4a03-85e9-b8c6f0aa7000");
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Тестирование метода getImageById")
    void shouldGetImageByIdSuccessfully() {

        Image image = new Image();
        image.setId(Id);

        ImageResponse imageResponse = new ImageResponse();

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(imageMapper.toImageResponse(image)).thenReturn(imageResponse);

        imageService.getImageById(image.getId());

        verify(imageRepository, times(1)).findById(Id);
        verify(imageMapper, times(1)).toImageResponse(image);
    }

    @Test
    @DisplayName("Успешная архивация изображения")
    void shouldArchiveImageSuccessfully() {
        Image image = new Image();
        image.setId(Id);
        image.setUserId(userId);
        imageRepository.save(image);

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(securityUtil.getCurrentUserId()).thenReturn(userId);
        imageService.archiveImage(image.getId());

        assertTrue(image.getIsArchived(), "Изображение должно быть архивировано");
    }

    @Test
    @DisplayName("Ошибка при архивации уже заархивированного изображения")
    void shouldThrowExceptionWhenImageAlreadyArchived() {
        Image image = new Image();
        image.setIsArchived(true);

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> imageService.archiveImage(image.getId()));

        assertEquals("Изображение уже заархивировано", exception.getMessage());
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    @DisplayName("Архивация изображения - чужое изображение - 403")
    void shouldThrowForbiddenWhenArchivingOthersImage() {
        Image image = new Image();
        image.setId(Id);
        image.setUserId(userId);
        imageRepository.save(image);

        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(securityUtil.getCurrentUserId()).thenReturn(UUID.randomUUID());

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class,
                () -> imageService.archiveImage(image.getId()));

        assertEquals("Архивировать можно только собственное изображение", exception.getMessage());
    }

    @Test
    @DisplayName("Изображение для архивации не найдено - 404")
    void shouldThrowImageNotFoundWhenArchiving() {
        UUID nonExistentImageId = UUID.randomUUID();

        when(imageRepository.findById(nonExistentImageId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> imageService.archiveImage(nonExistentImageId));

        assertEquals("Изображение не найдено", exception.getMessage());
    }
}
