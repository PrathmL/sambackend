package com.esspbackend.repository;

import com.esspbackend.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    
    // Basic finders
    Optional<Work> findByWorkCode(String workCode);
    
    List<Work> findBySchoolId(Long schoolId);
    
    List<Work> findBySchoolIdAndStatus(Long schoolId, String status);
    
    List<Work> findByStatus(String status);
    
    // Custom queries with ordering
    @Query("SELECT w FROM Work w WHERE w.schoolId = :schoolId ORDER BY w.createdAt DESC")
    List<Work> findBySchoolIdOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
    
    @Query("SELECT w FROM Work w WHERE w.status = :status ORDER BY w.createdAt DESC")
    List<Work> findByStatusOrderByCreatedAtDesc(@Param("status") String status);
    
    @Query("SELECT w FROM Work w WHERE w.workRequestId = :requestId")
    Optional<Work> findByWorkRequestId(@Param("requestId") Long requestId);
    
    // Find works with no recent updates
    @Query("SELECT w FROM Work w WHERE w.status = 'ACTIVE' AND w.lastUpdateAt < :date")
    List<Work> findWorksWithNoRecentUpdate(@Param("date") LocalDateTime date);
    
    @Query("SELECT w FROM Work w WHERE w.status = 'ACTIVE' AND w.lastUpdateAt IS NULL AND w.createdAt < :date")
    List<Work> findActiveWorksWithNoUpdates(@Param("date") LocalDateTime date);
    
    // Count queries
    long countBySchoolIdAndStatus(Long schoolId, String status);
    
    @Query("SELECT COUNT(w) FROM Work w WHERE w.schoolId = :schoolId AND w.status = 'ACTIVE'")
    long countActiveWorksBySchoolId(@Param("schoolId") Long schoolId);
    
    @Query("SELECT COUNT(w) FROM Work w WHERE w.schoolId = :schoolId AND w.status = 'COMPLETED'")
    long countCompletedWorksBySchoolId(@Param("schoolId") Long schoolId);
}