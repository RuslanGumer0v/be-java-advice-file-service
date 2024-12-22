package com.itm.advice.fileservice.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public abstract class AuditingEntity extends BaseEntity {

    @CreatedBy
    private UUID createdBy;

    @CreatedDate
    private LocalDateTime created;
}
