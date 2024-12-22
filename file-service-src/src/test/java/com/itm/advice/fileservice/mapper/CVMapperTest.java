package com.itm.advice.fileservice.mapper;

import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.response.CVResponse;
import org.bson.types.Binary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CVMapperTest {

    private final CVMapper cvMapper = Mappers.getMapper(CVMapper.class);
    private final UUID userId = UUID.fromString("c0f4bd7c-3b76-4a03-80e9-b8c6f0aa7dc2");
    private final UUID createdBy = UUID.fromString("dc533cff-e0bb-43ba-836a-c22f3a94ca6c");

    @Test
    @DisplayName("Должен корректно мапить поля из CVEntity в CVResponse")
    void CVEntityToCVResponse() {

        CV cv = new CV();
        cv.setUserId(userId);
        cv.setTitle("Test_CV");
        cv.setExtension("cv");
        cv.setFile(new Binary("some data".getBytes()));
        cv.setIsArchived(false);
        cv.setCreatedBy(createdBy);

        CVResponse response = cvMapper.toCVResponse(cv);

        assertThat(response.getFile()).isEqualTo(cv.getFile());
    }
}
