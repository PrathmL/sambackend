package com.esspbackend.controller;

import com.esspbackend.dto.WorkCreationRequest;
import com.esspbackend.dto.WorkDTO;
import com.esspbackend.dto.WorkProgressUpdateDTO;
import com.esspbackend.dto.WorkStageDTO;
import com.esspbackend.dto.PhotoDTO;
import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import com.esspbackend.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/works")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class WorkController {

    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private WorkStageRepository workStageRepository;
    
    @Autowired
    private WorkProgressUpdateRepository workProgressUpdateRepository;
    
    @Autowired
    private WorkProgressPhotoRepository workProgressPhotoRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WorkRequestRepository workRequestRepository;

    @Autowired
    private WorkRequestPhotoRepository workRequestPhotoRepository;
    
    @Autowired
    private FundSourceRepository fundSourceRepository;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private QuotationItemRepository quotationItemRepository;

    @Autowired
    private WorkProgressItemUsageRepository workProgressItemUsageRepository;

    @Autowired
    private SchoolInventoryRepository schoolInventoryRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AuditLogService auditLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String UPLOAD_DIR = "uploads/work-progress/";

    // ==================== GET ENDPOINTS ====================
    
    @GetMapping
    public ResponseEntity<List<WorkDTO>> getAllWorks() {
        List<Work> works = workRepository.findAll();
        works.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<WorkDTO>> getWorksBySchool(@PathVariable Long schoolId) {
        List<Work> works = workRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/taluka/{talukaId}")
    public ResponseEntity<List<WorkDTO>> getWorksByTaluka(@PathVariable Long talukaId) {
        // Get all schools in this taluka
        List<School> schools = schoolRepository.findByTalukaId(talukaId);
        List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
        
        // Get all works for these schools
        List<Work> works = new ArrayList<>();
        for (Long schoolId : schoolIds) {
            works.addAll(workRepository.findBySchoolId(schoolId));
        }
        works.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<WorkDTO>> getWorksByStatus(@PathVariable String status) {
        List<Work> works = workRepository.findByStatusOrderByCreatedAtDesc(status);
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/school/{schoolId}/status/{status}")
    public ResponseEntity<List<WorkDTO>> getWorksBySchoolAndStatus(
            @PathVariable Long schoolId, 
            @PathVariable String status) {
        List<Work> works = workRepository.findBySchoolIdAndStatus(schoolId, status);
        works.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkById(@PathVariable Long id) {
        return workRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-request/{requestId}")
    public ResponseEntity<?> getWorkByRequestId(@PathVariable Long requestId) {
        return workRepository.findByWorkRequestId(requestId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/fund-sources")
    public ResponseEntity<List<FundSource>> getFundSources(@PathVariable Long id) {
        List<FundSource> fundSources = fundSourceRepository.findByWorkId(id);
        return ResponseEntity.ok(fundSources);
    }
    
    @GetMapping("/{id}/stages")
    public ResponseEntity<List<WorkStage>> getStages(@PathVariable Long id) {
        List<WorkStage> stages = workStageRepository.findByWorkIdOrderByIdAsc(id);
        return ResponseEntity.ok(stages);
    }
    
    @GetMapping("/{id}/progress-updates")
    public ResponseEntity<List<WorkProgressUpdate>> getProgressUpdates(@PathVariable Long id) {
        List<WorkProgressUpdate> updates = workProgressUpdateRepository.findByWorkIdOrderByUpdatedAtDesc(id);
        return ResponseEntity.ok(updates);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<QuotationItem>> getWorkItems(@PathVariable Long id) {
        List<QuotationItem> items = quotationItemRepository.findByWorkId(id);
        return ResponseEntity.ok(items);
    }

    // ==================== CREATE ENDPOINTS ====================
    
    @PostMapping("/create-from-request")
    public ResponseEntity<?> createWorkFromRequest(@RequestBody WorkCreationRequest request) {
        try {
            // Validate total weightage is 100%
            int totalWeightage = request.getStages().stream()
                    .mapToInt(WorkCreationRequest.StageDTO::getWeightage)
                    .sum();
            if (totalWeightage != 100) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Total stage weightage must be 100%. Current total: " + totalWeightage + "%");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Validate total fund allocation equals sanctioned amount
            double totalAllocated = request.getFundSources().stream()
                    .mapToDouble(WorkCreationRequest.FundSourceDTO::getAmount)
                    .sum();
            if (Math.abs(totalAllocated - request.getSanctionedAmount()) > 0.01) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Total fund allocation (₹" + totalAllocated + 
                        ") must equal sanctioned amount (₹" + request.getSanctionedAmount() + ")");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Get the work request
            WorkRequest workRequest = workRequestRepository.findById(request.getWorkRequestId())
                    .orElseThrow(() -> new RuntimeException("Work request not found"));
            
            // Get school details
            School school = schoolRepository.findById(workRequest.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
            
            // Generate unique work code if not provided
            String workCode = request.getWorkCode();
            if (workCode == null || workCode.isEmpty()) {
                workCode = generateWorkCode();
            } else if (workRepository.existsByWorkCode(workCode)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Work code already exists: " + workCode);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            
            // Create work with ACTIVE status
            Work work = new Work();
            work.setWorkCode(workCode);
            work.setTitle(request.getTitle());
            work.setDescription(request.getDescription());
            work.setType(request.getType());
            work.setWorkRequestId(request.getWorkRequestId());
            work.setSchoolId(workRequest.getSchoolId());
            work.setTalukaId(school.getTalukaId());
            work.setSanctionedAmount(request.getSanctionedAmount());
            work.setTotalUtilized(0.0);
            work.setProgressPercentage(0);
            work.setStatus("ACTIVE");
            work.setActivatedAt(LocalDateTime.now());
            work.setLastUpdateAt(LocalDateTime.now());
            
            Work savedWork = workRepository.save(work);

            // Log work creation
            auditLogService.log("WORK_ACTIVATION", "New official work created: " + savedWork.getTitle() + " (" + savedWork.getWorkCode() + ")", null, "Admin", "ADMIN");
            
            // Create stages
            List<WorkStage> stages = new ArrayList<>();
            for (WorkCreationRequest.StageDTO stageDTO : request.getStages()) {
                WorkStage stage = new WorkStage();
                stage.setWorkId(savedWork.getId());
                stage.setName(stageDTO.getName());
                stage.setDescription(stageDTO.getDescription());
                stage.setWeightage(stageDTO.getWeightage());
                stage.setEstimatedDurationDays(stageDTO.getEstimatedDurationDays());
                stage.setProgressPercentage(0);
                stage.setStatus("PENDING");
                
                if (stageDTO.getEstimatedDurationDays() != null && stageDTO.getEstimatedDurationDays() > 0) {
                    stage.setExpectedCompletionDate(LocalDateTime.now().plusDays(stageDTO.getEstimatedDurationDays()));
                }
                stages.add(stage);
            }
            workStageRepository.saveAll(stages);
            
            // Create fund sources
            List<FundSource> fundSources = new ArrayList<>();
            for (WorkCreationRequest.FundSourceDTO fundDTO : request.getFundSources()) {
                FundSource fundSource = new FundSource();
                fundSource.setWorkId(savedWork.getId());
                fundSource.setSourceName(fundDTO.getSourceName());
                fundSource.setAmount(fundDTO.getAmount());
                fundSources.add(fundSource);
            }
            fundSourceRepository.saveAll(fundSources);
            
            // Update work request status
            workRequest.setStatus(WorkRequestStatus.WORK_CREATED);
            workRequestRepository.save(workRequest);

            // Link quotation items to this work
            quotationRepository.findByWorkRequestId(request.getWorkRequestId()).ifPresent(quotation -> {
                List<QuotationItem> qItems = quotationItemRepository.findByQuotationId(quotation.getId());
                for (QuotationItem qItem : qItems) {
                    qItem.setWorkId(savedWork.getId());
                    quotationItemRepository.save(qItem);
                }
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Work created and activated successfully");
            response.put("workId", savedWork.getId());
            response.put("workCode", savedWork.getWorkCode());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create work: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createWork(@RequestBody Work work) {
        try {
            if (work.getWorkCode() == null || work.getWorkCode().isEmpty()) {
                work.setWorkCode(generateWorkCode());
            } else if (workRepository.existsByWorkCode(work.getWorkCode())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Work code already exists: " + work.getWorkCode());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            
            work.setStatus("ACTIVE");
            work.setProgressPercentage(0);
            work.setTotalUtilized(0.0);
            work.setActivatedAt(LocalDateTime.now());
            work.setLastUpdateAt(LocalDateTime.now());
            
            Work saved = workRepository.save(work);

            // Log work creation
            auditLogService.log("WORK_CREATE", "Manual work created: " + saved.getTitle(), null, "Admin", "ADMIN");

            return ResponseEntity.ok(convertToDTO(saved));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create work: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ==================== UPDATE ENDPOINTS ====================
    
    @PostMapping("/progress")
    public ResponseEntity<?> updateProgress(
            @RequestParam("workId") Long workId,
            @RequestParam(value = "stageId", required = false) Long stageId,
            @RequestParam("progressPercentage") Integer progressPercentage,
            @RequestParam("remarks") String remarks,
            @RequestParam(value = "materialCost", required = false, defaultValue = "0") Double materialCost,
            @RequestParam(value = "laborCost", required = false, defaultValue = "0") Double laborCost,
            @RequestParam(value = "otherCost", required = false, defaultValue = "0") Double otherCost,
            @RequestParam("updatedById") Long updatedById,
            @RequestParam("updatedByRole") String updatedByRole,
            @RequestParam(value = "itemUsage", required = false) String itemUsageJson,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos,
            @RequestParam(value = "latitudes", required = false) Double[] latitudes,
            @RequestParam(value = "longitudes", required = false) Double[] longitudes) {
        
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Work work = workRepository.findById(workId)
                    .orElseThrow(() -> new RuntimeException("Work not found"));
            
            WorkProgressUpdate update = new WorkProgressUpdate();
            update.setWorkId(workId);
            update.setStageId(stageId);
            update.setProgressPercentage(progressPercentage);
            update.setRemarks(remarks);
            update.setMaterialCost(materialCost);
            update.setLaborCost(laborCost);
            update.setOtherCost(otherCost);
            update.setUpdatedById(updatedById);
            update.setUpdatedByRole(updatedByRole);
            
            WorkProgressUpdate savedUpdate = workProgressUpdateRepository.save(update);

            // Handle Item Usage and Inventory Update
            if (itemUsageJson != null && !itemUsageJson.isEmpty()) {
                List<Map<String, Object>> itemUsages = objectMapper.readValue(itemUsageJson, List.class);
                for (Map<String, Object> usageMap : itemUsages) {
                    Long materialId = usageMap.get("materialId") != null && !usageMap.get("materialId").toString().isEmpty() && !usageMap.get("materialId").toString().equals("null") ? 
                            Long.valueOf(usageMap.get("materialId").toString()) : null;
                    String materialName = usageMap.get("materialName") != null ? usageMap.get("materialName").toString() : "Unknown";
                    Double qtyUsed = Double.valueOf(usageMap.get("quantityUsed").toString());
                    
                    WorkProgressItemUsage usage = new WorkProgressItemUsage();
                    usage.setWorkProgressUpdateId(savedUpdate.getId());
                    usage.setMaterialId(materialId);
                    usage.setMaterialName(materialName);
                    usage.setQuantityUsed(qtyUsed);
                    workProgressItemUsageRepository.save(usage);
                    
                    // Update School Inventory only if materialId exists
                    if (materialId != null) {
                        schoolInventoryRepository.findBySchoolIdAndMaterialId(work.getSchoolId(), materialId)
                                .ifPresent(inventory -> {
                                    inventory.setCurrentQuantity(inventory.getCurrentQuantity() - qtyUsed);
                                    schoolInventoryRepository.save(inventory);
                                    
                                    // Check for low stock alert
                                    if (inventory.getCurrentQuantity() <= inventory.getReorderLevel()) {
                                        Alert alert = new Alert();
                                        alert.setTitle("Low Item Alert: " + materialName);
                                        alert.setMessage("Item quantity is low for school #" + work.getSchoolId() + ". Current: " + inventory.getCurrentQuantity());
                                        alert.setType("WARNING");
                                        alert.setCategory("LOW_INVENTORY");
                                        alert.setRole(Role.CLERK);
                                        alert.setSchoolId(work.getSchoolId());
                                        alert.setRelatedId(inventory.getId());
                                        alertRepository.save(alert);
                                    }
                                });
                    }
                }
            }

            // Log progress update
            userRepository.findById(updatedById).ifPresent(user -> {
                auditLogService.log("WORK_PROGRESS_UPDATE", "Progress updated for: " + work.getTitle() + " (" + progressPercentage + "%)", user.getId(), user.getName(), user.getRole().name());
            });
            
            if (photos != null && photos.length > 0) {
                for (int i = 0; i < photos.length; i++) {
                    MultipartFile photo = photos[i];
                    String fileName = "wp_" + savedUpdate.getId() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath, photo.getBytes());
                    
                    WorkProgressPhoto progressPhoto = new WorkProgressPhoto();
                    progressPhoto.setProgressUpdateId(savedUpdate.getId());
                    progressPhoto.setPhotoUrl("/uploads/work-progress/" + fileName);
                    
                    // Save geotag if available
                    if (latitudes != null && latitudes.length > i && longitudes != null && longitudes.length > i) {
                        progressPhoto.setGeoLocation(latitudes[i] + "," + longitudes[i]);
                    }
                    
                    workProgressPhotoRepository.save(progressPhoto);
                }
            }
            
            double totalUtilized = (work.getTotalUtilized() != null ? work.getTotalUtilized() : 0) 
                    + materialCost + laborCost + otherCost;
            work.setTotalUtilized(totalUtilized);
            
            // If updating a specific stage, recalculate overall progress based on weights
            if (stageId != null) {
                WorkStage stage = workStageRepository.findById(stageId)
                        .orElseThrow(() -> new RuntimeException("Stage not found"));
                
                stage.setProgressPercentage(progressPercentage);
                if (progressPercentage >= 100) {
                    stage.setStatus("COMPLETED");
                    stage.setCompletedAt(LocalDateTime.now());
                    if (stage.getActualDurationDays() == null && stage.getStartedAt() != null) {
                        long days = java.time.Duration.between(stage.getStartedAt(), LocalDateTime.now()).toDays();
                        stage.setActualDurationDays((int) days);
                    }
                } else if (progressPercentage > 0) {
                    stage.setStatus("IN_PROGRESS");
                    if (stage.getStartedAt() == null) {
                        stage.setStartedAt(LocalDateTime.now());
                    }
                }
                stage.setRemarks(remarks);
                workStageRepository.save(stage);
                
                // Recalculate overall work progress
                List<WorkStage> allStages = workStageRepository.findByWorkId(workId);
                double calculatedProgress = 0;
                for (WorkStage s : allStages) {
                    int sWeight = (s.getWeightage() != null) ? s.getWeightage() : 0;
                    int sProgress = (s.getProgressPercentage() != null) ? s.getProgressPercentage() : 0;
                    calculatedProgress += (sProgress * sWeight) / 100.0;
                }
                work.setProgressPercentage((int) Math.round(calculatedProgress));
            } else {
                // If no stageId, set overall progress directly
                work.setProgressPercentage(progressPercentage);
            }
            
            work.setLastUpdateAt(LocalDateTime.now());
            
            if (work.getProgressPercentage() != null && work.getProgressPercentage() >= 100) {
                work.setStatus("COMPLETED");
                work.setCompletedAt(LocalDateTime.now());
            } else if (work.getProgressPercentage() != null && work.getProgressPercentage() > 0) {
                work.setStatus("IN_PROGRESS");
            }
            
            workRepository.save(work);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Progress updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload photos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWork(@PathVariable Long id, @RequestBody Work workDetails) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setTitle(workDetails.getTitle());
                    work.setDescription(workDetails.getDescription());
                    work.setType(workDetails.getType());
                    work.setSanctionedAmount(workDetails.getSanctionedAmount());
                    Work updated = workRepository.save(work);

                    // Log work update
                    auditLogService.log("WORK_UPDATE", "Work details updated: " + updated.getTitle(), null, "Admin", "ADMIN");

                    return ResponseEntity.ok(convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== WORK STATUS ENDPOINTS ====================
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateWork(@PathVariable Long id) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setStatus("ACTIVE");
                    work.setActivatedAt(LocalDateTime.now());
                    work.setLastUpdateAt(LocalDateTime.now());
                    workRepository.save(work);

                    // Log activation
                    auditLogService.log("WORK_ACTIVATION", "Work activated: " + work.getTitle(), null, "Admin", "ADMIN");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work activated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/suspend")
    public ResponseEntity<?> suspendWork(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setStatus("ON_HOLD");
                    workRepository.save(work);

                    // Log suspension
                    auditLogService.log("WORK_SUSPEND", "Work suspended: " + work.getTitle(), null, "Admin", "ADMIN");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work suspended: " + body.getOrDefault("reason", "No reason provided"));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/resume")
    public ResponseEntity<?> resumeWork(@PathVariable Long id) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setStatus("ACTIVE");
                    workRepository.save(work);

                    // Log resume
                    auditLogService.log("WORK_RESUME", "Work resumed: " + work.getTitle(), null, "Admin", "ADMIN");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work resumed");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/notes")
    public ResponseEntity<?> saveInternalNotes(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setInternalNotes(body.get("notes"));
                    workRepository.save(work);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Internal notes saved successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/mark-complete")
    public ResponseEntity<?> markWorkComplete(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return workRepository.findById(id)
                .map(work -> {
                    if (work.getProgressPercentage() < 100) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Work progress is " + work.getProgressPercentage() + "%. Complete all stages first.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                    
                    work.setStatus("PENDING_CLOSURE");
                    work.setCompletedAt(LocalDateTime.now());
                    workRepository.save(work);

                    // Log completion
                    auditLogService.log("WORK_COMPLETE_REQUEST", "Work marked as complete: " + work.getTitle(), null, "HM/Sachiv", "USER");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work marked as complete. Awaiting Sachiv verification.");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyWork(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return workRepository.findById(id)
                .map(work -> {
                    if (!"PENDING_CLOSURE".equals(work.getStatus())) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Work must be in PENDING_CLOSURE status to verify");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                    }
                    
                    work.setStatus("COMPLETED");
                    workRepository.save(work);

                    // Log verification
                    auditLogService.log("WORK_VERIFIED", "Work verified and closed: " + work.getTitle(), null, "Sachiv", "SACHIV");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work verified and closed successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== DELETE ENDPOINTS ====================
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWork(@PathVariable Long id) {
        return workRepository.findById(id)
                .map(work -> {
                    work.setStatus("DELETED");
                    workRepository.save(work);

                    // Log deletion
                    auditLogService.log("WORK_DELETE", "Work deleted: " + work.getTitle(), null, "Admin", "ADMIN");

                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work deleted successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== HELPER METHODS ====================
    
    private String generateWorkCode() {
        String prefix = "WRK";
        String year = String.valueOf(LocalDateTime.now().getYear()).substring(2);
        String maxCode = workRepository.findMaxWorkCodeByPrefix(prefix + year);
        
        int nextNumber = 1;
        if (maxCode != null && maxCode.length() >= 8) {
            try {
                String numPart = maxCode.substring(6);
                nextNumber = Integer.parseInt(numPart) + 1;
            } catch (NumberFormatException e) {
                nextNumber = 1;
            }
        }
        
        return String.format("%s%s%04d", prefix, year, nextNumber);
    }
    
    private WorkDTO convertToDTO(Work work) {
        WorkDTO dto = new WorkDTO();
        dto.setId(work.getId());
        dto.setWorkCode(work.getWorkCode());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setType(work.getType());
        dto.setWorkRequestId(work.getWorkRequestId());
        dto.setSchoolId(work.getSchoolId());
        dto.setSanctionedAmount(work.getSanctionedAmount());
        dto.setTotalUtilized(work.getTotalUtilized());
        dto.setProgressPercentage(work.getProgressPercentage());
        dto.setStatus(work.getStatus());
        dto.setCreatedAt(work.getCreatedAt());
        dto.setActivatedAt(work.getActivatedAt());
        dto.setCompletedAt(work.getCompletedAt());
        dto.setLastUpdateAt(work.getLastUpdateAt());
        dto.setInternalNotes(work.getInternalNotes());
        
        // Get school name
        schoolRepository.findById(work.getSchoolId()).ifPresent(school -> {
            dto.setSchoolName(school.getName());
        });

        // Get initial photos from work request
        if (work.getWorkRequestId() != null) {
            List<WorkRequestPhoto> requestPhotos = workRequestPhotoRepository.findByWorkRequestIdOrderByOrderIndexAsc(work.getWorkRequestId());
            dto.setPhotoUrls(requestPhotos.stream()
                    .map(p -> new PhotoDTO(p.getPhotoUrl(), p.getCaption(), p.getLatitude(), p.getLongitude()))
                    .collect(Collectors.toList()));
        }
        
        List<WorkStage> stages = workStageRepository.findByWorkIdOrderByIdAsc(work.getId());
        List<WorkStageDTO> stageDTOs = stages.stream()
                .map(this::convertStageToDTO)
                .collect(Collectors.toList());
        dto.setStages(stageDTOs);
        
        List<WorkProgressUpdate> updates = workProgressUpdateRepository.findByWorkIdOrderByUpdatedAtDesc(work.getId());
        List<WorkProgressUpdateDTO> updateDTOs = updates.stream()
                .map(this::convertUpdateToDTO)
                .collect(Collectors.toList());
        dto.setProgressUpdates(updateDTOs);
        
        return dto;
    }
    
    private WorkStageDTO convertStageToDTO(WorkStage stage) {
        WorkStageDTO dto = new WorkStageDTO();
        dto.setId(stage.getId());
        dto.setName(stage.getName());
        dto.setDescription(stage.getDescription());
        dto.setWeightage(stage.getWeightage());
        dto.setEstimatedDurationDays(stage.getEstimatedDurationDays());
        dto.setActualDurationDays(stage.getActualDurationDays());
        dto.setProgressPercentage(stage.getProgressPercentage());
        dto.setStatus(stage.getStatus());
        dto.setExpectedCompletionDate(stage.getExpectedCompletionDate());
        dto.setActualCompletionDate(stage.getActualCompletionDate());
        dto.setRemarks(stage.getRemarks());
        return dto;
    }
    
    private WorkProgressUpdateDTO convertUpdateToDTO(WorkProgressUpdate update) {
        WorkProgressUpdateDTO dto = new WorkProgressUpdateDTO();
        dto.setId(update.getId());
        dto.setWorkId(update.getWorkId());
        dto.setStageId(update.getStageId());
        dto.setProgressPercentage(update.getProgressPercentage());
        dto.setRemarks(update.getRemarks());
        dto.setMaterialCost(update.getMaterialCost());
        dto.setLaborCost(update.getLaborCost());
        dto.setOtherCost(update.getOtherCost());
        dto.setTotalCost(update.getTotalCost());
        dto.setUpdatedAt(update.getUpdatedAt());
        
        if (update.getStageId() != null) {
            workStageRepository.findById(update.getStageId())
                    .ifPresent(stage -> dto.setStageName(stage.getName()));
        }
        
        userRepository.findById(update.getUpdatedById())
                .ifPresent(user -> dto.setUpdatedBy(user.getName()));
        
        List<WorkProgressPhoto> photos = workProgressPhotoRepository.findByProgressUpdateId(update.getId());
        dto.setPhotoUrls(photos.stream()
                .map(p -> new PhotoDTO(p.getPhotoUrl(), p.getCaption(), p.getGeoLocation()))
                .collect(Collectors.toList()));
        
        dto.setItemUsage(workProgressItemUsageRepository.findByWorkProgressUpdateId(update.getId()));
        
        return dto;
    }
}
