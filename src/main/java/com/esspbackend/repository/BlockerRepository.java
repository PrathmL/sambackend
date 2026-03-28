package com.esspbackend.repository;

import com.esspbackend.entity.Blocker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface BlockerRepository extends JpaRepository<Blocker, Long> {
    
    // Basic finders
    List<Blocker> findBySchoolId(Long schoolId);
    List<Blocker> findByWorkId(Long workId);
    List<Blocker> findByStatus(String status);
    List<Blocker> findByPriority(String priority);
    List<Blocker> findByAssignedToId(Long assignedToId);
    
    // Order by creation date
    @Query("SELECT b FROM Blocker b WHERE b.schoolId = :schoolId ORDER BY b.createdAt DESC")
    List<Blocker> findBySchoolIdOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
    
    @Query("SELECT b FROM Blocker b WHERE b.workId = :workId ORDER BY b.createdAt DESC")
    List<Blocker> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'NEW' ORDER BY b.createdAt DESC")
    List<Blocker> findNewBlockersOrderByCreatedAtDesc();
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'NEW' AND b.priority = 'HIGH' ORDER BY b.createdAt DESC")
    List<Blocker> findHighPriorityNewBlockers();
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'IN_PROGRESS' ORDER BY b.updatedAt ASC")
    List<Blocker> findInProgressBlockersByOldestFirst();
    
    @Query("SELECT b FROM Blocker b WHERE b.assignedToId = :userId AND b.status != 'RESOLVED' ORDER BY b.createdAt DESC")
    List<Blocker> findAssignedBlockers(@Param("userId") Long userId);
    
    // Count queries
    long countByStatus(String status);
    long countBySchoolIdAndStatus(Long schoolId, String status);
    long countByAssignedToIdAndStatus(Long assignedToId, String status);
    
    @Query("SELECT COUNT(b) FROM Blocker b WHERE b.schoolId = :schoolId AND b.status != 'RESOLVED'")
    long countActiveBlockersBySchoolId(@Param("schoolId") Long schoolId);
    
    // Analytics queries
    @Query("SELECT b.type, COUNT(b) FROM Blocker b GROUP BY b.type ORDER BY COUNT(b) DESC")
    List<Object[]> countBlockersByType();
    
    @Query("SELECT b.priority, COUNT(b) FROM Blocker b WHERE b.status != 'RESOLVED' GROUP BY b.priority")
    List<Object[]> countActiveBlockersByPriority();
    
    @Query("SELECT b.status, COUNT(b) FROM Blocker b GROUP BY b.status")
    List<Object[]> countBlockersByStatus();
    
    @Query("SELECT FUNCTION('DATE', b.createdAt), COUNT(b) FROM Blocker b WHERE b.createdAt >= :startDate GROUP BY FUNCTION('DATE', b.createdAt) ORDER BY FUNCTION('DATE', b.createdAt)")
    List<Object[]> getBlockerTrend(@Param("startDate") LocalDateTime startDate);
    
    // MySQL compatible average resolution time query
    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, b.createdAt, b.resolvedAt) / 86400.0) FROM Blocker b WHERE b.resolvedAt IS NOT NULL AND b.createdAt >= :startDate")
    Double getAverageResolutionTime(@Param("startDate") LocalDateTime startDate);
    
    // Alternative: Get average resolution time in days using H2/MySQL compatible function
    @Query("SELECT AVG(DATEDIFF(b.resolvedAt, b.createdAt)) FROM Blocker b WHERE b.resolvedAt IS NOT NULL AND b.createdAt >= :startDate")
    Double getAverageResolutionTimeDays(@Param("startDate") LocalDateTime startDate);
    
    // Update methods
    @Modifying
    @Transactional
    @Query("UPDATE Blocker b SET b.status = :status, b.assignedToId = :assignedToId, b.assignedToRole = :assignedToRole WHERE b.id = :id")
    void assignBlocker(@Param("id") Long id, @Param("status") String status, @Param("assignedToId") Long assignedToId, @Param("assignedToRole") String assignedToRole);
    
    @Modifying
    @Transactional
    @Query("UPDATE Blocker b SET b.status = 'RESOLVED', b.resolutionNotes = :notes, b.resolvedAt = :resolvedAt WHERE b.id = :id")
    void resolveBlocker(@Param("id") Long id, @Param("notes") String notes, @Param("resolvedAt") LocalDateTime resolvedAt);
    
    @Modifying
    @Transactional
    @Query("UPDATE Blocker b SET b.status = 'ESCALATED', b.escalatedAt = :escalatedAt, b.escalatedToId = :toId, b.escalatedToRole = :toRole, b.escalationReason = :reason WHERE b.id = :id")
    void escalateBlocker(@Param("id") Long id, @Param("escalatedAt") LocalDateTime escalatedAt, @Param("toId") Long toId, @Param("toRole") String toRole, @Param("reason") String reason);
}