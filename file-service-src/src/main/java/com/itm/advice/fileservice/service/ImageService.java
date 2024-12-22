package com.itm.advice.fileservice.service;

import com.itm.advice.fileservice.response.ImageResponse;

import java.util.UUID;

public interface ImageService {

    ImageResponse getImageById(UUID id);

    void archiveImage(UUID imageId);
}
