package com.esspbackend.repository;

import com.esspbackend.entity.Alert;
import com.esspbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    @Query("SELECT a FROM Alert a WHERE (a.userId = :userId OR (a.role = :role AND (a.schoolId = :schoolId OR a.schoolId IS NULL) AND (a.talukaId = :talukaId OR a.talukaId IS NULL))) AND (a.snoozedUntil IS NULL OR a.snoozedUntil <= :now) AND a.status != 'RESOLVED' ORDER BY a.createdAt DESC")
    List<Alert> findActiveAlerts(
        @Param("userId") Long userId, 
        @Param("role") Role role, 
        @Param("schoolId") Long schoolId, 
        @Param("talukaId") Long talukaId,
        @Param("now") LocalDateTime now
    );

    long countByUserIdAndStatus(Long userId, String status);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE (a.userId = :userId OR (a.role = :role AND (a.schoolId = :schoolId OR a.schoolId IS NULL) AND (a.talukaId = :talukaId OR a.talukaId IS NULL))) AND a.status = 'UNREAD'")
    long countUnreadAlerts(
        @Param("userId") Long userId, 
        @Param("role") Role role, 
        @Param("schoolId") Long schoolId, 
        @Param("talukaId") Long talukaId
    );

    @Modifying
    @Transactional
    @Query("UPDATE Alert a SET a.status = :status WHERE a.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying
    @Transactional
    @Query("UPDATE Alert a SET a.status = 'SNOOZED', a.snoozedUntil = :snoozedUntil WHERE a.id = :id")
    void snoozeAlert(@Param("id") Long id, @Param("snoozedUntil") LocalDateTime snoozedUntil);

    boolean existsByCategoryAndRelatedIdAndStatusNot(String category, Long relatedId, String status);
}
