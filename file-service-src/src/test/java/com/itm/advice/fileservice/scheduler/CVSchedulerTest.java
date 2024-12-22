package com.itm.advice.fileservice.scheduler;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.repository.CVRepository;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.mongodb.assertions.Assertions.assertFalse;
import static com.mongodb.assertions.Assertions.assertTrue;

public class CVSchedulerTest extends BaseIntegrationTest {

    @Autowired
    private CVScheduler cvScheduler;

    @Autowired
    private CVRepository cvRepository;

    @Test
    @Description("Удаляет CV, у которых флаг isArchived = true, и дата архивации больше текущей даты-времени более чем на 30 дней")
    public void testCVScheduler() {
        var expiredAndArchivedCV = new CV();
        expiredAndArchivedCV.setIsArchived(true);
        expiredAndArchivedCV.setArchivedDate(LocalDateTime.now().minusDays(200));

        var notArchivedCV = new CV();
        notArchivedCV.setIsArchived(false);
        notArchivedCV.setArchivedDate(LocalDateTime.now().minusDays(200));

        var notExpiredCV = new CV();
        notExpiredCV.setIsArchived(true);
        notExpiredCV.setArchivedDate(LocalDateTime.now().minusDays(20));

        var savedExpiredAndArchivedCV = cvRepository.save(expiredAndArchivedCV);
        var savedNotArchivedCV = cvRepository.save(notArchivedCV);
        var savedNotExpiredCV = cvRepository.save(notExpiredCV);

        cvScheduler.cleanupArchived();

        var actual = cvRepository.findAll();

        assertTrue(actual.contains(savedNotExpiredCV));
        assertTrue(actual.contains(savedNotArchivedCV));
        assertFalse(actual.contains(savedExpiredAndArchivedCV));
    }
}
