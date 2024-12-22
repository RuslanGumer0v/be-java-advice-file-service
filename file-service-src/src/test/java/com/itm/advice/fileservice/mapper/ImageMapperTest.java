package com.itm.advice.fileservice.mapper;

import com.itm.advice.fileservice.BaseIntegrationTest;
import com.itm.advice.fileservice.domain.entity.Image;
import com.itm.advice.fileservice.response.ImageResponse;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageMapperTest extends BaseIntegrationTest {

    private final UUID userId = UUID.fromString("c0f4bd7c-3b76-4a03-80e9-b8c6f0aa7dc2");
    private final UUID createdBy = UUID.fromString("dc533cff-e0bb-43ba-836a-c22f3a94ca6c");
    private final ImageMapper imageMapper = Mappers.getMapper(ImageMapper.class);

    @Test
    @DisplayName("Должен корректно мапить поля из ImageEntity в ImageResponse")
    void ImageEntityToImageResponse() {

        Image image = new Image();
        image.setUserId(userId);
        image.setTitle("Test_Image");
        image.setExtension("Image");
        image.setFile(new Binary("some data".getBytes()));
        image.setIsArchived(false);
        image.setCreatedBy(createdBy);

        ImageResponse response = imageMapper.toImageResponse(image);

        assertThat(response.getFile()).isEqualTo(image.getFile());
    }
}
