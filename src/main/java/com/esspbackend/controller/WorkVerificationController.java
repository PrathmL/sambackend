package com.esspbackend.controller;

import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import com.esspbackend.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/works/verification")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class WorkVerificationController {

    private final WorkRepository workRepository;
    private final WorkInspectionRepository inspectionRepository;
    private final PunchListItemRepository punchListItemRepository;
    private final HandoverCertificateRepository certificateRepository;
    private final AuditLogService auditLogService;

    public WorkVerificationController(WorkRepository workRepository, 
                                    WorkInspectionRepository inspectionRepository,
                                    PunchListItemRepository punchListItemRepository,
                                    HandoverCertificateRepository certificateRepository,
                                    AuditLogService auditLogService) {
        this.workRepository = workRepository;
        this.inspectionRepository = inspectionRepository;
        this.punchListItemRepository = punchListItemRepository;
        this.certificateRepository = certificateRepository;
        this.auditLogService = auditLogService;
    }

    // ==================== INSPECTION ====================

    @PostMapping("/{workId}/inspection")
    public ResponseEntity<?> saveInspection(@PathVariable Long workId, @RequestBody WorkInspection inspection) {
        return workRepository.findById(workId).map(work -> {
            inspection.setWorkId(workId);
            WorkInspection saved = inspectionRepository.save(inspection);
            
            auditLogService.log("WORK_INSPECTION", "Inspection recorded for: " + work.getWorkCode(), null, "Sachiv", "SACHIV");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{workId}/inspection")
    public ResponseEntity<?> getInspection(@PathVariable Long workId) {
        return inspectionRepository.findByWorkId(workId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== PUNCH LIST ====================

    @PostMapping("/{workId}/punch-list")
    public ResponseEntity<?> addPunchListItem(@PathVariable Long workId, @RequestBody PunchListItem item) {
        return workRepository.findById(workId).map(work -> {
            item.setWorkId(workId);
            item.setStatus("Open");
            PunchListItem saved = punchListItemRepository.save(item);
            
            auditLogService.log("PUNCH_LIST_ADD", "Punch list item added for: " + work.getWorkCode(), null, "Sachiv", "SACHIV");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{workId}/punch-list")
    public ResponseEntity<List<PunchListItem>> getPunchList(@PathVariable Long workId) {
        return ResponseEntity.ok(punchListItemRepository.findByWorkId(workId));
    }

    @PutMapping("/punch-list/{itemId}/resolve")
    public ResponseEntity<?> resolvePunchListItem(@PathVariable Long itemId, @RequestBody Map<String, String> body) {
        return punchListItemRepository.findById(itemId).map(item -> {
            item.setStatus("Resolved");
            item.setResolutionDate(LocalDateTime.now());
            item.setRemarks(body.get("remarks"));
            item.setPhotoAfter(body.get("photoAfter"));
            PunchListItem updated = punchListItemRepository.save(item);
            
            auditLogService.log("PUNCH_LIST_RESOLVE", "Punch list item resolved for work ID: " + item.getWorkId(), null, "HM/Sachiv", "USER");
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==================== HANDOVER CERTIFICATE ====================

    @PostMapping("/{workId}/certificate")
    public ResponseEntity<?> generateCertificate(@PathVariable Long workId, @RequestBody HandoverCertificate certificate) {
        return workRepository.findById(workId).map(work -> {
            // Validate: All punch list items must be resolved
            List<PunchListItem> openItems = punchListItemRepository.findByWorkIdAndStatus(workId, "Open");
            if (!openItems.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot generate certificate. " + openItems.size() + " punch list items are still open."));
            }

            certificate.setWorkId(workId);
            certificate.setCertificateNumber("CERT-" + work.getWorkCode() + "-" + System.currentTimeMillis() % 10000);
            certificate.setFinalCost(work.getTotalUtilized());
            certificate.setCompletionDate(work.getCompletedAt());
            
            HandoverCertificate saved = certificateRepository.save(certificate);
            
            // Mark work as COMPLETED
            work.setStatus("COMPLETED");
            workRepository.save(work);
            
            auditLogService.log("WORK_CERTIFICATE_GENERATE", "Handover certificate generated for: " + work.getWorkCode(), null, "Sachiv", "SACHIV");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{workId}/certificate")
    public ResponseEntity<?> getCertificate(@PathVariable Long workId) {
        return certificateRepository.findByWorkId(workId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
