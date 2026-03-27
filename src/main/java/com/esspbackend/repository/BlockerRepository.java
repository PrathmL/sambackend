package com.esspbackend.repository;

import com.esspbackend.entity.Blocker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BlockerRepository extends JpaRepository<Blocker, Long> {
    
    // Basic finders
    List<Blocker> findBySchoolId(Long schoolId);
    
    List<Blocker> findByWorkId(Long workId);
    
    List<Blocker> findByStatus(String status);
    
    List<Blocker> findByPriority(String priority);
    
    // Custom queries with ordering
    @Query("SELECT b FROM Blocker b WHERE b.schoolId = :schoolId ORDER BY b.createdAt DESC")
    List<Blocker> findBySchoolIdOrderByCreatedAtDesc(@Param("schoolId") Long schoolId);
    
    @Query("SELECT b FROM Blocker b WHERE b.workId = :workId ORDER BY b.createdAt DESC")
    List<Blocker> findByWorkIdOrderByCreatedAtDesc(@Param("workId") Long workId);
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'NEW' ORDER BY b.createdAt DESC")
    List<Blocker> findNewBlockersOrderByCreatedAtDesc();
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'NEW' AND b.priority = 'High' ORDER BY b.createdAt DESC")
    List<Blocker> findHighPriorityNewBlockers();
    
    @Query("SELECT b FROM Blocker b WHERE b.status = 'IN_PROGRESS' ORDER BY b.updatedAt ASC")
    List<Blocker> findInProgressBlockersByOldestFirst();
    
    // Count queries
    long countByStatus(String status);
    
    long countBySchoolIdAndStatus(Long schoolId, String status);
    
    @Query("SELECT COUNT(b) FROM Blocker b WHERE b.schoolId = :schoolId AND b.status != 'RESOLVED'")
    long countActiveBlockersBySchoolId(@Param("schoolId") Long schoolId);
    
    @Query("SELECT b.type, COUNT(b) FROM Blocker b GROUP BY b.type ORDER BY COUNT(b) DESC")
    List<Object[]> countBlockersByType();
    
    @Query("SELECT b.priority, COUNT(b) FROM Blocker b WHERE b.status != 'RESOLVED' GROUP BY b.priority")
    List<Object[]> countActiveBlockersByPriority();
}