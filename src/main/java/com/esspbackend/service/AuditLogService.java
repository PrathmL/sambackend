package com.esspbackend.service;

import com.esspbackend.entity.AuditLog;
import com.esspbackend.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String details, Long userId, String userName, String userRole) {
        AuditLog log = new AuditLog(action, details, userId, userName, userRole);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc();
    }
}
