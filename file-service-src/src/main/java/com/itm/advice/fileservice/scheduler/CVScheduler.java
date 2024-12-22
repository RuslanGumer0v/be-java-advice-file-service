package com.itm.advice.fileservice.scheduler;

import com.itm.advice.fileservice.service.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CVScheduler {

    private static final int OFFSET = 30;

    private final CVService cvService;

    @Scheduled(cron = "${cv.cleanup.cron}")
    public void cleanupArchived() {
        var dateTime = LocalDateTime.now().minusDays(OFFSET);
        cvService.deleteAllArchivedAndExpiredCVs(dateTime);
    }
}
