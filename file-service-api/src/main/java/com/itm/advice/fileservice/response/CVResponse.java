package com.itm.advice.fileservice.response;

import org.bson.types.Binary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVResponse {

    private Binary file;

}
