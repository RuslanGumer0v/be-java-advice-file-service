package com.itm.advice.fileservice.scheduler;

import com.itm.advice.fileservice.service.ImageCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageScheduler {

    private static final int OFFSET = 30;

    private final ImageCleanupService imageCleanupService;

    @Scheduled(cron = "${image.cleanup.cron}")
    public void cleanupArchived() {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(OFFSET);

        log.debug("Image cleanup scheduler started. Threshold date: {}", thresholdDate);

        try {
            imageCleanupService.deleteArchivedImagesOlderThan(thresholdDate);
            log.debug("Image cleanup completed successfully for images older than {}", thresholdDate);
        } catch (Exception e) {
            log.error("Error occurred during image cleanup", e);
        }
    }
}
