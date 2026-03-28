package com.esspbackend.repository;

import com.esspbackend.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    
    Optional<Work> findByWorkCode(String workCode);
    
    List<Work> findBySchoolId(Long schoolId);
    
    @Query("SELECT w FROM Work w WHERE w.schoolId = :schoolId ORDER BY w.createdAt DESC")
    List<Work> findBySchoolIdOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
    
    List<Work> findBySchoolIdAndStatus(Long schoolId, String status);
    
    List<Work> findByStatus(String status);
    
    @Query("SELECT w FROM Work w WHERE w.status = :status ORDER BY w.createdAt DESC")
    List<Work> findByStatusOrderByCreatedAtDesc(@Param("status") String status);
    
    @Query("SELECT w FROM Work w WHERE w.workRequestId = :requestId")
    Optional<Work> findByWorkRequestId(@Param("requestId") Long requestId);
    
    @Query("SELECT w FROM Work w WHERE w.status = 'ACTIVE' AND w.lastUpdateAt < :date")
    List<Work> findWorksWithNoRecentUpdate(@Param("date") LocalDateTime date);
    
    boolean existsByWorkCode(String workCode);
    
    @Query("SELECT MAX(w.workCode) FROM Work w WHERE w.workCode LIKE :prefix%")
    String findMaxWorkCodeByPrefix(@Param("prefix") String prefix);
    
    long countBySchoolIdAndStatus(Long schoolId, String status);
    
    @Query("SELECT w FROM Work w WHERE w.createdAt BETWEEN :startDate AND :endDate ORDER BY w.createdAt DESC")
    List<Work> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}