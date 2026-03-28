package com.esspbackend.repository;

import com.esspbackend.entity.Communication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunicationRepository extends JpaRepository<Communication, Long> {
    List<Communication> findByTalukaIdOrderByCreatedAtDesc(Long talukaId);
    List<Communication> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);
    List<Communication> findByTalukaIdAndIsBulkTrueOrderByCreatedAtDesc(Long talukaId);
    List<Communication> findByTalukaIdIsNullAndIsBulkTrueOrderByCreatedAtDesc();
    List<Communication> findBySenderIdOrderByCreatedAtDesc(Long senderId);
}
