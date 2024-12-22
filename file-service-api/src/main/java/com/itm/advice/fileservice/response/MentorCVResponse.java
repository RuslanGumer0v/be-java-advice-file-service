package com.itm.advice.fileservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MentorCVResponse {

    private UUID id;

    private UUID userId;

    private String title;

    private String extension;

    @JsonProperty(value = "isArchived")
    private boolean isArchived;

    private LocalDateTime archiveDate;

    private UUID createdBy;

    private LocalDateTime created;
}