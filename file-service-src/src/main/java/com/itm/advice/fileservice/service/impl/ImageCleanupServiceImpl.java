package com.itm.advice.fileservice.service.impl;

import com.itm.advice.fileservice.repository.ImageRepository;
import com.itm.advice.fileservice.service.ImageCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImageCleanupServiceImpl implements ImageCleanupService {
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void deleteArchivedImagesOlderThan(LocalDateTime thresholdDate) {
        var imagesToDelete = imageRepository.findAllByIsArchivedTrueAndArchivedDateBefore(thresholdDate);
        imageRepository.deleteAll(imagesToDelete);
    }
}
