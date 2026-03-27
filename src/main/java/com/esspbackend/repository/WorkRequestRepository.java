package com.esspbackend.repository;

import com.esspbackend.entity.WorkRequest;
import com.esspbackend.entity.WorkRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface WorkRequestRepository extends JpaRepository<WorkRequest, Long> {
    
    // Basic finders
    List<WorkRequest> findBySchoolId(Long schoolId);
    
    List<WorkRequest> findByStatus(WorkRequestStatus status);
    
    List<WorkRequest> findBySchoolIdAndStatus(Long schoolId, WorkRequestStatus status);
    
    // Custom queries with ordering
    @Query("SELECT w FROM WorkRequest w WHERE w.schoolId = :schoolId ORDER BY w.createdAt DESC")
    List<WorkRequest> findBySchoolIdOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
    
    @Query("SELECT w FROM WorkRequest w WHERE w.createdById = :userId ORDER BY w.createdAt DESC")
    List<WorkRequest> findByCreatedByIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT w FROM WorkRequest w WHERE w.createdById = :userId AND w.status = :status ORDER BY w.createdAt DESC")
    List<WorkRequest> findByCreatedByIdAndStatusOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("status") WorkRequestStatus status);
    
    @Query("SELECT w FROM WorkRequest w WHERE w.schoolId = :schoolId AND w.status = :status ORDER BY w.createdAt DESC")
    List<WorkRequest> findBySchoolIdAndStatusOrderByCreatedAtDesc(@Param("schoolId") Long schoolId, @Param("status") WorkRequestStatus status);
    
    // Count queries - only one version of each method
    @Query("SELECT COUNT(w) FROM WorkRequest w WHERE w.status = :status")
    long countByStatus(@Param("status") WorkRequestStatus status);
    
    @Query("SELECT COUNT(w) FROM WorkRequest w WHERE w.schoolId = :schoolId AND w.status = :status")
    long countBySchoolIdAndStatus(@Param("schoolId") Long schoolId, @Param("status") WorkRequestStatus status);
}