package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.response.MentorCVResponse;
import com.itm.advice.fileservice.service.CVService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ModeratorCVControllerImpl implements ModeratorCVController {

    private final CVService cvService;

    @Override
    public MentorCVResponse deleteCV(UUID id) {
        return cvService.deleteCVByIdWithRoleModerator(id);
    }
}
