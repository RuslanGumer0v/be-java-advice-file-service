package com.itm.advice.fileservice.service.impl;

import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.exception.FileProcessingException;
import com.itm.advice.fileservice.exception.InvalidIDException;
import com.itm.advice.fileservice.exception.NotFoundException;
import com.itm.advice.fileservice.kafka.producer.CvEventProducer;
import com.itm.advice.fileservice.mapper.CVMapper;
import com.itm.advice.fileservice.repository.CVRepository;
import com.itm.advice.fileservice.response.CVResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import com.itm.advice.fileservice.service.CVService;
import com.itm.advice.fileservice.util.SecurityUtil;
import com.itm.space.itmadvicecommonmodels.kafka.model.CvEvent;
import com.itm.space.itmadvicecommonmodels.kafka.model.CvEventType;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CVServiceImpl implements CVService {

    private final CVRepository cvRepository;
    private final CVMapper cvMapper;
    private final SecurityUtil securityUtil;
    private final CvEventProducer cvEventProducer;

    @Override
    @Transactional(readOnly = true)
    public CVResponse getCVById(UUID id) {
        CV cv = cvRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Резюме не найдено"));
        return cvMapper.toCVResponse(cv);
    }

    @Override
    @Transactional
    public UUID saveOrReplaceCv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("Файл резюме пустой");
        }

        UUID userId = securityUtil.getCurrentUserId();

        CV newOrExistingCv = cvRepository.findByUserId(userId).orElse(new CV());

        try {
            newOrExistingCv.setFile(new Binary(file.getBytes()));
            newOrExistingCv.setUserId(userId);
            newOrExistingCv.setTitle(file.getOriginalFilename());
            newOrExistingCv.setIsArchived(false);
            newOrExistingCv = cvRepository.save(newOrExistingCv);

            CvEvent cvEvent = new CvEvent();
            cvEvent.setUserId(userId);
            cvEvent.setId(newOrExistingCv.getId());
            cvEvent.setType(CvEventType.CV_CREATED);
            cvEventProducer.produce(cvEvent);

            return newOrExistingCv.getId();
        } catch (IOException e) {
            throw new FileProcessingException("Ошибка при обработке файла", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MentorCVResponse> getMentorCVList(boolean archived, Pageable pageable) {
        Page<CV> cvs = archived
                ? cvRepository.findAll(pageable)
                : cvRepository.findAllByIsArchived(false, pageable);
        return cvs.map(cvMapper::toMentorCVResponse);
    }

    @Override
    public MentorCVResponse deleteCVByIdWithRoleModerator(UUID id) {
        CV cv = cvRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("CV not found for id: " + id));

        cv.setIsArchived(true);
        cv.setArchivedDate(LocalDateTime.now());

        return cvMapper.toMentorCVResponse(cvRepository.save(cv));
    }

    @Override
    @Transactional
    public CVResponse deleteCvByID(UUID id) {
        CV cv = cvRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Резюме не найдено"));

        UUID userId = securityUtil.getCurrentUserId();

        if (!cv.getUserId().equals(userId)) {
            throw new InvalidIDException("Нельзя удалить чужое резюме");
        }

        cv.setIsArchived(true);
        cv.setArchivedDate(LocalDateTime.now());
        cv = cvRepository.save(cv);

        return cvMapper.toCVResponse(cv);
    }

    @Override
    public void deleteAllArchivedAndExpiredCVs(LocalDateTime date) {
        cvRepository.deleteAllByIsArchivedTrueAndArchivedDateBefore(date);
    }
}