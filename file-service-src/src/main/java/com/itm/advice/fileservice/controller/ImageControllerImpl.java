package com.itm.advice.fileservice.controller;

import com.itm.advice.fileservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class ImageControllerImpl implements ImageController {

    private final ImageService imageService;

    @Override
    public ResponseEntity<ByteArrayResource> getImage(UUID id) {

        byte[] data = imageService.getImageById(id).getFile().getData();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .headers(httpHeaders -> httpHeaders.set("Content-Disposition", "attachment; filename=\"Image.jpeg\""))
                .body(new ByteArrayResource(data));
    }

    @Override
    public ResponseEntity<Void> archiveImage(UUID id) {
        imageService.archiveImage(id);
        return ResponseEntity.ok().build();
    }
}
