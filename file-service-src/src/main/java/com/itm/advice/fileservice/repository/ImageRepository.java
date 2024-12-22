package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.domain.entity.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends MongoRepository<Image, UUID> {
    List<Image> findAllByIsArchivedTrueAndArchivedDateBefore(LocalDateTime date);
}
