package com.itm.advice.fileservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

    private Binary file;
}
