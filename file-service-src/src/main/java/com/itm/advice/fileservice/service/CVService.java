package com.itm.advice.fileservice.service;

import com.itm.advice.fileservice.response.CVResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CVService {

    CVResponse getCVById(UUID id);

    UUID saveOrReplaceCv(MultipartFile file);

    CVResponse deleteCvByID(UUID id);

    Page<MentorCVResponse> getMentorCVList(boolean archived, Pageable pageable);

    MentorCVResponse deleteCVByIdWithRoleModerator(UUID id);

    void deleteAllArchivedAndExpiredCVs(LocalDateTime date);
}