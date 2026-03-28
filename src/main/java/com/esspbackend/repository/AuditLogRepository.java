package com.esspbackend.repository;

import com.esspbackend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);
    List<AuditLog> findByPerformedByRoleOrderByCreatedAtDesc(String role);
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
