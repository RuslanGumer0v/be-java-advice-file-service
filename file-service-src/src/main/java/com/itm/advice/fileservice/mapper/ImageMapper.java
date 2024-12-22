package com.itm.advice.fileservice.mapper;

import com.itm.advice.fileservice.domain.entity.Image;
import com.itm.advice.fileservice.response.ImageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ImageMapper {

    @Mapping(target = "file", source = "file")
    ImageResponse toImageResponse(Image image);
}