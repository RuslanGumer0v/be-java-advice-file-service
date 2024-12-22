package com.itm.advice.fileservice.service;

import java.time.LocalDateTime;

public interface ImageCleanupService {
    void deleteArchivedImagesOlderThan(LocalDateTime thresholdDate);
}
