package com.esspbackend.repository;

import com.esspbackend.entity.WorkInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkInspectionRepository extends JpaRepository<WorkInspection, Long> {
    Optional<WorkInspection> findByWorkId(Long workId);
}
