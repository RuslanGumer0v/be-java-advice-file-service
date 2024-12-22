package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.exception.InvalidFileFormatException;
import com.itm.advice.fileservice.response.CVResponse;
import com.itm.advice.fileservice.response.CVUploadResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import com.itm.advice.fileservice.service.CVService;
import com.itm.advice.fileservice.util.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class CVControllerImpl implements CVController {

    private final CVService cvService;

    @Override
    public ResponseEntity<ByteArrayResource> getCV(UUID id) {

        byte[] data = cvService.getCVById(id).getFile().getData();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .headers(httpHeaders -> httpHeaders.set("Content-Disposition", "attachment; filename=\"CV.pdf\""))
                .body(new ByteArrayResource(data));
    }

    @Override
    public CVUploadResponse uploadFile(@RequestParam("file") MultipartFile file) {
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new InvalidFileFormatException("Допустимы только PDF файлы");
        }

        UUID cvId = cvService.saveOrReplaceCv(file);
        return new CVUploadResponse(cvId);
    }

    @Override
    public ResponseEntity<List<MentorCVResponse>> getMentorCVList(boolean archived, int page, int size) {
        Pageable pageable = PageUtils.pageable(page, size, "created");
        Page<MentorCVResponse> mentorCVPage = cvService.getMentorCVList(archived, pageable);

        return ResponseEntity.ok(mentorCVPage.getContent());
    }

    @Override
    public ResponseEntity<ByteArrayResource> deleteCV(UUID id) {
        CVResponse cvResponse = cvService.deleteCvByID(id);

        byte[] data = cvResponse.getFile().getData();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_PDF)
                .headers(httpHeaders -> httpHeaders.set("Content-Disposition", "attachment; filename=\"CV.pdf\""))
                .body(new ByteArrayResource(data));
    }
}
