package com.esspbackend.controller;

import com.esspbackend.dto.WorkCompletionRequest;
import com.esspbackend.dto.WorkDTO;
import com.esspbackend.dto.WorkProgressUpdateDTO;
import com.esspbackend.dto.WorkStageDTO;
import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/works")
@CrossOrigin(origins = "http://localhost:3000")
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

    private final String UPLOAD_DIR = "uploads/work-progress/";

   
 // Get works by school
    @GetMapping
    public ResponseEntity<List<WorkDTO>> getWorks(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String status) {
        
        List<Work> works;
        
        if (schoolId != null && status != null) {
            works = workRepository.findBySchoolIdAndStatus(schoolId, status);
        } else if (schoolId != null) {
            works = workRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        } else if (status != null) {
            works = workRepository.findByStatus(status);
        } else {
            works = workRepository.findAll();
        }
        
        List<WorkDTO> dtos = works.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    
    
    // Get work by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkById(@PathVariable Long id) {
        return workRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update work progress
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
            @RequestParam(value = "photos", required = false) MultipartFile[] photos) {
        
        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Get work
            Work work = workRepository.findById(workId)
                    .orElseThrow(() -> new RuntimeException("Work not found"));
            
            // Create progress update
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
            
            // Save photos
            if (photos != null && photos.length > 0) {
                for (int i = 0; i < photos.length; i++) {
                    MultipartFile photo = photos[i];
                    String fileName = "wp_" + savedUpdate.getId() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath, photo.getBytes());
                    
                    WorkProgressPhoto progressPhoto = new WorkProgressPhoto();
                    progressPhoto.setProgressUpdateId(savedUpdate.getId());
                    progressPhoto.setPhotoUrl("/uploads/work-progress/" + fileName);
                    workProgressPhotoRepository.save(progressPhoto);
                }
            }
            
            // Update work total utilized and progress
            double totalUtilized = work.getTotalUtilized() + materialCost + laborCost + otherCost;
            work.setTotalUtilized(totalUtilized);
            work.setProgressPercentage(progressPercentage);
            work.setLastUpdateAt(LocalDateTime.now());
            workRepository.save(work);
            
            // Update stage progress if stageId provided
            if (stageId != null) {
                workStageRepository.findById(stageId).ifPresent(stage -> {
                    stage.setProgressPercentage(progressPercentage);
                    if (progressPercentage >= 100) {
                        stage.setStatus("COMPLETED");
                        stage.setCompletedAt(LocalDateTime.now());
                    } else if (progressPercentage > 0) {
                        stage.setStatus("IN_PROGRESS");
                        if (stage.getStartedAt() == null) {
                            stage.setStartedAt(LocalDateTime.now());
                        }
                    }
                    workStageRepository.save(stage);
                });
            }
            
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
    
    // Mark work as complete
    @PostMapping("/mark-complete")
    public ResponseEntity<?> markWorkComplete(@RequestBody WorkCompletionRequest request) {
        return workRepository.findById(request.getWorkId())
                .map(work -> {
                    work.setStatus("PENDING_CLOSURE");
                    work.setCompletedAt(LocalDateTime.now());
                    workRepository.save(work);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work marked as complete. Awaiting Sachiv verification.");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    private WorkDTO convertToDTO(Work work) {
        WorkDTO dto = new WorkDTO();
        dto.setId(work.getId());
        dto.setWorkCode(work.getWorkCode());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setType(work.getType());
        dto.setSanctionedAmount(work.getSanctionedAmount());
        dto.setTotalUtilized(work.getTotalUtilized());
        dto.setProgressPercentage(work.getProgressPercentage());
        dto.setStatus(work.getStatus());
        dto.setCreatedAt(work.getCreatedAt());
        dto.setActivatedAt(work.getActivatedAt());
        dto.setLastUpdateAt(work.getLastUpdateAt());
        
        // Get stages
        List<WorkStage> stages = workStageRepository.findByWorkIdOrderByIdAsc(work.getId());
        List<WorkStageDTO> stageDTOs = stages.stream().map(this::convertStageToDTO).collect(Collectors.toList());
        dto.setStages(stageDTOs);
        
        // Get progress updates
        List<WorkProgressUpdate> updates = workProgressUpdateRepository.findByWorkIdOrderByUpdatedAtDesc(work.getId());
        List<WorkProgressUpdateDTO> updateDTOs = updates.stream().map(this::convertUpdateToDTO).collect(Collectors.toList());
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
        
        // Get stage name
        if (update.getStageId() != null) {
            workStageRepository.findById(update.getStageId())
                    .ifPresent(stage -> dto.setStageName(stage.getName()));
        }
        
        // Get user name
        userRepository.findById(update.getUpdatedById())
                .ifPresent(user -> dto.setUpdatedBy(user.getName()));
        
        // Get photos
        List<WorkProgressPhoto> photos = workProgressPhotoRepository.findByProgressUpdateId(update.getId());
        dto.setPhotoUrls(photos.stream().map(WorkProgressPhoto::getPhotoUrl).collect(Collectors.toList()));
        
        return dto;
    }
}