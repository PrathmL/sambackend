package com.esspbackend.repository;

import com.esspbackend.entity.WorkRequestPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WorkRequestPhotoRepository extends JpaRepository<WorkRequestPhoto, Long> {
    
    @Query("SELECT p FROM WorkRequestPhoto p WHERE p.workRequestId = :requestId ORDER BY p.orderIndex ASC")
    List<WorkRequestPhoto> findByWorkRequestIdOrderByOrderIndexAsc(@Param("requestId") Long workRequestId);
    
    void deleteByWorkRequestId(Long workRequestId);
}