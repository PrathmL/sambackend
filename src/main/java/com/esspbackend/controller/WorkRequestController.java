package com.esspbackend.controller;

import com.esspbackend.dto.QuotationDTO;
import com.esspbackend.dto.WorkRequestDTO;
import com.esspbackend.dto.PhotoDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/work-requests")
@CrossOrigin(origins = "http://localhost:3000")
public class WorkRequestController {

    @Autowired
    private WorkRequestRepository workRequestRepository;
    
    @Autowired
    private WorkRequestPhotoRepository workRequestPhotoRepository;
    
    @Autowired
    private QuotationRepository quotationRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private AuditLogService auditLogService;

    private final String UPLOAD_DIR = "uploads/work-requests/";

    // Create new work request with photos
    @PostMapping
    public ResponseEntity<?> createWorkRequest(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type") String type,
            @RequestParam("category") String category,
            @RequestParam("priority") String priority,
            @RequestParam("schoolId") Long schoolId,
            @RequestParam("createdById") Long createdById,
            @RequestParam(value = "expectedTimeline", required = false) String expectedTimeline,
            @RequestParam(value = "photos", required = false) MultipartFile[] photos,
            @RequestParam(value = "latitudes", required = false) Double[] latitudes,
            @RequestParam(value = "longitudes", required = false) Double[] longitudes) {
        
        try {
            // Create directory if not exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Create work request
            WorkRequest workRequest = new WorkRequest();
            workRequest.setTitle(title);
            workRequest.setDescription(description);
            workRequest.setType(type);
            workRequest.setCategory(category);
            workRequest.setPriority(priority);
            workRequest.setSchoolId(schoolId);
            workRequest.setCreatedById(createdById);
            workRequest.setExpectedTimeline(expectedTimeline);
            workRequest.setStatus(WorkRequestStatus.PENDING_QUOTATION);
            
            WorkRequest savedRequest = workRequestRepository.save(workRequest);

            // Log work request creation
            userRepository.findById(createdById).ifPresent(user -> {
                auditLogService.log("WORK_REQUEST_CREATE", "New work request submitted: " + title, user.getId(), user.getName(), user.getRole().name());
            });
            
            // Create Alert for Clerk
            Alert alert = new Alert();
            alert.setTitle("New Work Request: " + title);
            alert.setMessage("A new work request has been submitted and needs quotation.");
            alert.setType("INFO");
            alert.setCategory("WORK_REQUEST");
            alert.setRole(Role.CLERK);
            alert.setSchoolId(schoolId);
            alert.setRelatedId(savedRequest.getId());
            alertRepository.save(alert);
            
            // Save photos
            if (photos != null && photos.length > 0) {
                for (int i = 0; i < photos.length; i++) {
                    MultipartFile photo = photos[i];
                    String fileName = "wr_" + savedRequest.getId() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath, photo.getBytes());
                    
                    WorkRequestPhoto workRequestPhoto = new WorkRequestPhoto();
                    workRequestPhoto.setWorkRequestId(savedRequest.getId());
                    workRequestPhoto.setPhotoUrl("/uploads/work-requests/" + fileName);
                    workRequestPhoto.setOrderIndex(i);
                    
                    // Set Geotags if available
                    if (latitudes != null && i < latitudes.length) {
                        workRequestPhoto.setLatitude(latitudes[i]);
                    }
                    if (longitudes != null && i < longitudes.length) {
                        workRequestPhoto.setLongitude(longitudes[i]);
                    }
                    
                    workRequestPhotoRepository.save(workRequestPhoto);
                }
            }
            
            return ResponseEntity.ok(convertToDTO(savedRequest));
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload photos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Get work requests by school or user
    @GetMapping
    public ResponseEntity<List<WorkRequestDTO>> getWorkRequests(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) WorkRequestStatus status) {
        
        List<WorkRequest> requests = new ArrayList<>();
        
        if (schoolId != null && status != null) {
            requests = workRequestRepository.findBySchoolIdAndStatusOrderByCreatedAtDesc(schoolId, status);
        } else if (schoolId != null) {
            requests = workRequestRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        } else if (userId != null && status != null) {
            requests = workRequestRepository.findByCreatedByIdAndStatusOrderByCreatedAtDesc(userId, status);
        } else if (userId != null) {
            requests = workRequestRepository.findByCreatedByIdOrderByCreatedAtDesc(userId);
        } else if (status != null) {
            requests = workRequestRepository.findByStatus(status);
            requests.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else {
            requests = workRequestRepository.findAll();
            requests.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }
        
        List<WorkRequestDTO> dtos = requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    // Get work request by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkRequestById(@PathVariable Long id) {
        return workRequestRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get quotation for work request
    @GetMapping("/{id}/quotation")
    public ResponseEntity<?> getQuotation(@PathVariable Long id) {
        Optional<Quotation> quotation = quotationRepository.findByWorkRequestId(id);
        if (quotation.isPresent()) {
            QuotationDTO dto = new QuotationDTO();
            dto.setId(quotation.get().getId());
            dto.setWorkRequestId(quotation.get().getWorkRequestId());
            dto.setMaterialCost(quotation.get().getMaterialCost());
            dto.setLaborCost(quotation.get().getLaborCost());
            dto.setAdditionalCosts(quotation.get().getAdditionalCosts());
            dto.setGrandTotal(quotation.get().getGrandTotal());
            dto.setMaterialDetails(quotation.get().getMaterialDetails());
            dto.setSubmittedAt(quotation.get().getSubmittedAt());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
    
    // Approve work request
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        return workRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(WorkRequestStatus.APPROVED);
                    request.setApprovedAt(LocalDateTime.now());
                    if (body != null && body.containsKey("remarks")) {
                        request.setAdminRemarks(body.get("remarks"));
                    }
                    workRequestRepository.save(request);

                    // Log approval
                    auditLogService.log("WORK_REQUEST_APPROVE", "Work request approved: " + request.getTitle(), null, "Admin", "ADMIN");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work request approved successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Reject work request
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return workRequestRepository.findById(id)
                .map(request -> {
                    request.setStatus(WorkRequestStatus.REJECTED);
                    request.setRejectedAt(LocalDateTime.now());
                    request.setRejectionReason(body.get("reason"));
                    request.setAdminRemarks(body.get("reason")); // Also store in adminRemarks for consistency if needed
                    workRequestRepository.save(request);

                    // Log rejection
                    auditLogService.log("WORK_REQUEST_REJECT", "Work request rejected: " + request.getTitle(), null, "Admin", "ADMIN");
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Work request rejected");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    private WorkRequestDTO convertToDTO(WorkRequest request) {
        WorkRequestDTO dto = new WorkRequestDTO();
        dto.setId(request.getId());
        dto.setTitle(request.getTitle());
        dto.setDescription(request.getDescription());
        dto.setType(request.getType());
        dto.setCategory(request.getCategory());
        dto.setPriority(request.getPriority());
        dto.setSchoolId(request.getSchoolId());
        dto.setStatus(request.getStatus().name());
        dto.setRejectionReason(request.getRejectionReason());
        dto.setAdminRemarks(request.getAdminRemarks());
        dto.setExpectedTimeline(request.getExpectedTimeline());
        dto.setApprovedAt(request.getApprovedAt());
        dto.setRejectedAt(request.getRejectedAt());
        dto.setCreatedAt(request.getCreatedAt());
        
        // Get school name
        schoolRepository.findById(request.getSchoolId())
                .ifPresent(school -> dto.setSchoolName(school.getName()));
        
        // Get photos
        List<WorkRequestPhoto> photos = workRequestPhotoRepository.findByWorkRequestIdOrderByOrderIndexAsc(request.getId());
        dto.setPhotoUrls(photos.stream()
                .map(p -> new PhotoDTO(p.getPhotoUrl(), p.getCaption(), p.getLatitude(), p.getLongitude()))
                .collect(Collectors.toList()));
        
        // Get quotation if exists
        quotationRepository.findByWorkRequestId(request.getId()).ifPresent(quotation -> {
            QuotationDTO quotationDTO = new QuotationDTO();
            quotationDTO.setId(quotation.getId());
            quotationDTO.setWorkRequestId(quotation.getWorkRequestId());
            quotationDTO.setMaterialCost(quotation.getMaterialCost());
            quotationDTO.setLaborCost(quotation.getLaborCost());
            quotationDTO.setAdditionalCosts(quotation.getAdditionalCosts());
            quotationDTO.setGrandTotal(quotation.getGrandTotal());
            quotationDTO.setMaterialDetails(quotation.getMaterialDetails());
            quotationDTO.setSubmittedAt(quotation.getSubmittedAt());
            dto.setQuotation(quotationDTO);
        });
        
        return dto;
    }
}
