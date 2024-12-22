package com.itm.advice.fileservice.service.impl;

import com.itm.advice.fileservice.domain.entity.Image;
import com.itm.advice.fileservice.exception.NotFoundException;
import com.itm.advice.fileservice.exception.PermissionDeniedException;
import com.itm.advice.fileservice.mapper.ImageMapper;
import com.itm.advice.fileservice.repository.ImageRepository;
import com.itm.advice.fileservice.response.ImageResponse;
import com.itm.advice.fileservice.service.ImageService;
import com.itm.advice.fileservice.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private ImageRepository imageRepository;
    private ImageMapper imageMapper;
    private SecurityUtil securityUtil;

    @Override
    @Transactional(readOnly = true)
    public ImageResponse getImageById(UUID id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Изображение не найдено"));

        if (Boolean.TRUE.equals(image.getIsArchived())) {
            throw new NotFoundException("Изображение архивное");
        }
        return imageMapper.toImageResponse(image);
    }

    @Override
    @Transactional
    public void archiveImage(UUID imageId) {
        Image image = imageRepository
                .findById(imageId).orElseThrow(() -> new NotFoundException("Изображение не найдено"));

        if (Boolean.TRUE.equals(image.getIsArchived())) {
            throw new NotFoundException("Изображение уже заархивировано");
        }

        UUID currentUserId = securityUtil.getCurrentUserId();
        if (!image.getUserId().equals(currentUserId)) {
            throw new PermissionDeniedException("Архивировать можно только собственное изображение");
        }

        image.setIsArchived(true);
        imageRepository.save(image);
    }
}
