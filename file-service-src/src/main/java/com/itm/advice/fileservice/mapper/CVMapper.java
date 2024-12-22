package com.itm.advice.fileservice.mapper;

import com.itm.advice.fileservice.domain.entity.CV;
import com.itm.advice.fileservice.response.CVResponse;
import com.itm.advice.fileservice.response.MentorCVResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CVMapper {

    @Mapping(source = "file", target = "file")
    CVResponse toCVResponse(CV cv);

    @Mapping(source = "isArchived", target = "archived")
    @Mapping(source = "archivedDate", target = "archiveDate")
    MentorCVResponse toMentorCVResponse(CV cv);
}