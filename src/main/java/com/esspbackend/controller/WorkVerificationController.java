package com.esspbackend.controller;

import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import com.esspbackend.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/works/verification")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkVerificationController {

    @Autowired
    private WorkRepository workRepository;
    @Autowired
    private WorkInspectionRepository inspectionRepository;
    @Autowired
    private PunchListItemRepository punchListItemRepository;
    @Autowired
    private HandoverCertificateRepository certificateRepository;
    @Autowired
    private VerificationPhotoRepository verificationPhotoRepository;
    @Autowired
    private AuditLogService auditLogService;

    private final String UPLOAD_DIR = "uploads/verification/";

    // ==================== STEP 1: INSPECTION ====================

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
                .orElse(ResponseEntity.ok(null));
    }

    // ==================== STEP 2: PUNCH LIST ====================

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

    // ==================== STEP 3: FINAL DOCUMENTATION (PHOTOS) ====================

    @PostMapping("/{workId}/photos")
    public ResponseEntity<?> uploadVerificationPhotos(
            @PathVariable Long workId,
            @RequestParam("photos") MultipartFile[] photos,
            @RequestParam(value = "latitudes", required = false) Double[] latitudes,
            @RequestParam(value = "longitudes", required = false) Double[] longitudes,
            @RequestParam(value = "captions", required = false) String[] captions) {
        
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<VerificationPhoto> savedPhotos = new ArrayList<>();
            for (int i = 0; i < photos.length; i++) {
                MultipartFile photo = photos[i];
                String fileName = "ver_" + workId + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, photo.getBytes());

                VerificationPhoto verPhoto = new VerificationPhoto();
                verPhoto.setWorkId(workId);
                verPhoto.setPhotoUrl("/uploads/verification/" + fileName);
                
                if (captions != null && i < captions.length) verPhoto.setCaption(captions[i]);
                if (latitudes != null && i < latitudes.length) verPhoto.setLatitude(latitudes[i]);
                if (longitudes != null && i < longitudes.length) verPhoto.setLongitude(longitudes[i]);

                savedPhotos.add(verificationPhotoRepository.save(verPhoto));
            }

            return ResponseEntity.ok(savedPhotos);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload photos");
        }
    }

    @GetMapping("/{workId}/photos")
    public ResponseEntity<List<VerificationPhoto>> getVerificationPhotos(@PathVariable Long workId) {
        return ResponseEntity.ok(verificationPhotoRepository.findByWorkId(workId));
    }

    @GetMapping("/{workId}/certificate")
    public ResponseEntity<?> getCertificate(@PathVariable Long workId) {
        return certificateRepository.findByWorkId(workId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(null));
    }

    // ==================== STEP 4 & 5: HANDOVER & SETTLEMENT ====================

    @PostMapping("/{workId}/finalize")
    public ResponseEntity<?> finalizeWorkClosure(@PathVariable Long workId, @RequestBody HandoverCertificate certificate) {
        return workRepository.findById(workId).map(work -> {
            // Validate: All punch list items must be resolved
            List<PunchListItem> openItems = punchListItemRepository.findByWorkIdAndStatus(workId, "Open");
            if (!openItems.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot finalize. " + openItems.size() + " punch list items are still open."));
            }

            // Validate: Mandatory photos (min 2)
            List<VerificationPhoto> photos = verificationPhotoRepository.findByWorkId(workId);
            if (photos.size() < 2) {
                return ResponseEntity.badRequest().body(Map.of("error", "At least 2 mandatory inspection photos are required."));
            }

            certificate.setWorkId(workId);
            certificate.setCertificateNumber("CERT-" + work.getWorkCode() + "-" + (System.currentTimeMillis() % 10000));
            
            // Financial Settlement logic (if provided in certificate object)
            if (certificate.getActualExpenditure() == null) {
                certificate.setActualExpenditure(work.getTotalUtilized());
            }
            certificate.setSanctionedFunds(work.getSanctionedAmount());
            certificate.setVariance(work.getSanctionedAmount() - certificate.getActualExpenditure());
            certificate.setUnspentFunds(Math.max(0, certificate.getVariance()));
            
            HandoverCertificate saved = certificateRepository.save(certificate);
            
            // Mark work as COMPLETED
            work.setStatus("COMPLETED");
            work.setCompletedAt(LocalDateTime.now());
            workRepository.save(work);
            
            auditLogService.log("WORK_FINALIZE", "Work finalized and completed: " + work.getWorkCode(), null, "Sachiv", "SACHIV");
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}
