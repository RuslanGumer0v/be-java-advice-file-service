package com.itm.advice.fileservice.repository;

import com.itm.advice.fileservice.domain.entity.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CVRepository extends MongoRepository<CV, UUID> {

    Optional<CV> findByUserId(UUID userId);

    Page<CV> findAllByIsArchived(boolean isArchived, Pageable pageable);

    void deleteAllByIsArchivedTrueAndArchivedDateBefore(LocalDateTime date);
}
