package com.itm.advice.fileservice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "attachment")
@AllArgsConstructor
@NoArgsConstructor
public class Attachment extends BaseEntity {

    private UUID userId;

    private String title;

    private String extension;

    private Binary file;

    private Boolean isArchived;

    private LocalDateTime archivedDate;

    private UUID createdBy;

    @CreatedDate
    private LocalDateTime created;

}
