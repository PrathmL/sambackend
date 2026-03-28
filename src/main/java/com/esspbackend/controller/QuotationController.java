package com.esspbackend.controller;

import com.esspbackend.dto.QuotationDTO;
import com.esspbackend.entity.Quotation;
import com.esspbackend.entity.WorkRequest;
import com.esspbackend.entity.WorkRequestStatus;
import com.esspbackend.entity.Alert;
import com.esspbackend.entity.Role;
import com.esspbackend.repository.QuotationRepository;
import com.esspbackend.repository.WorkRequestRepository;
import com.esspbackend.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class QuotationController {

    @Autowired
    private QuotationRepository quotationRepository;
    
    @Autowired
    private WorkRequestRepository workRequestRepository;

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping("/pending/{schoolId}")
    public ResponseEntity<List<QuotationDTO>> getPendingQuotations(@PathVariable Long schoolId) {
        List<Quotation> quotations = quotationRepository.findPendingQuotationsBySchool(schoolId);
        List<QuotationDTO> dtos = quotations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> createQuotation(@RequestBody Map<String, Object> request) {
        try {
            // Extract data from request
            Long workRequestId = Long.valueOf(request.get("workRequestId").toString());
            Long schoolId = Long.valueOf(request.get("schoolId").toString());
            Long preparedById = Long.valueOf(request.get("preparedById").toString());
            
            Double materialCost = request.containsKey("materialCost") ? 
                    Double.valueOf(request.get("materialCost").toString()) : 0.0;
            Double laborCost = request.containsKey("laborCost") ? 
                    Double.valueOf(request.get("laborCost").toString()) : 0.0;
            Double additionalCosts = request.containsKey("additionalCosts") ? 
                    Double.valueOf(request.get("additionalCosts").toString()) : 0.0;
            
            String materialDetails = request.containsKey("materialDetails") ? 
                    (String) request.get("materialDetails") : "";
            String laborDetails = request.containsKey("laborDetails") ? 
                    (String) request.get("laborDetails") : "";
            String additionalDetails = request.containsKey("additionalDetails") ? 
                    (String) request.get("additionalDetails") : "";
            
            LocalDateTime validUntil = null;
            if (request.containsKey("validUntil") && request.get("validUntil") != null && !request.get("validUntil").toString().isEmpty()) {
                validUntil = LocalDateTime.parse(request.get("validUntil").toString() + "T00:00:00");
            }
            
            // Calculate grand total
            Double grandTotal = materialCost + laborCost + additionalCosts;
            
            // Check if work request exists
            WorkRequest workRequest = workRequestRepository.findById(workRequestId)
                    .orElseThrow(() -> new RuntimeException("Work request not found"));
            
            // Create quotation
            Quotation quotation = new Quotation();
            quotation.setWorkRequestId(workRequestId);
            quotation.setSchoolId(schoolId);
            quotation.setPreparedById(preparedById);
            quotation.setMaterialCost(materialCost);
            quotation.setLaborCost(laborCost);
            quotation.setAdditionalCosts(additionalCosts);
            quotation.setGrandTotal(grandTotal);
            quotation.setMaterialDetails(materialDetails);
            quotation.setLaborDetails(laborDetails);
            quotation.setAdditionalDetails(additionalDetails);
            quotation.setValidUntil(validUntil);
            quotation.setStatus("PENDING");
            quotation.setSubmittedAt(LocalDateTime.now());
            
            Quotation saved = quotationRepository.save(quotation);
            
            // Create Alert for Admin
            Alert alert = new Alert();
            alert.setTitle("New Quotation Submitted");
            alert.setMessage("A new quotation has been prepared for request #" + workRequestId + " and needs approval.");
            alert.setType("WARNING");
            alert.setCategory("WORK_REQUEST");
            alert.setRole(Role.ADMIN);
            alert.setRelatedId(saved.getId());
            alertRepository.save(alert);
            
            // Update work request status
            workRequest.setStatus(WorkRequestStatus.PENDING_APPROVAL);
            workRequestRepository.save(workRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Quotation submitted successfully");
            response.put("quotation", convertToDTO(saved));
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create quotation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveQuotation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return quotationRepository.findById(id)
                .map(quotation -> {
                    quotation.setStatus("APPROVED");
                    quotation.setApprovedAt(LocalDateTime.now());
                    quotation.setAdminRemarks(body.get("remarks"));
                    quotationRepository.save(quotation);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Quotation approved successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectQuotation(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return quotationRepository.findById(id)
                .map(quotation -> {
                    quotation.setStatus("REJECTED");
                    quotation.setRejectedAt(LocalDateTime.now());
                    quotation.setAdminRemarks(body.get("remarks"));
                    quotationRepository.save(quotation);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Quotation rejected");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private QuotationDTO convertToDTO(Quotation quotation) {
        QuotationDTO dto = new QuotationDTO();
        dto.setId(quotation.getId());
        dto.setWorkRequestId(quotation.getWorkRequestId());
        dto.setSchoolId(quotation.getSchoolId());
        dto.setMaterialCost(quotation.getMaterialCost());
        dto.setLaborCost(quotation.getLaborCost());
        dto.setAdditionalCosts(quotation.getAdditionalCosts());
        dto.setGrandTotal(quotation.getGrandTotal());
        dto.setMaterialDetails(quotation.getMaterialDetails());
        dto.setLaborDetails(quotation.getLaborDetails());
        dto.setAdditionalDetails(quotation.getAdditionalDetails());
        dto.setValidUntil(quotation.getValidUntil());
        dto.setStatus(quotation.getStatus());
        dto.setAdminRemarks(quotation.getAdminRemarks());
        dto.setSubmittedAt(quotation.getSubmittedAt());
        dto.setApprovedAt(quotation.getApprovedAt());
        dto.setRejectedAt(quotation.getRejectedAt());
        return dto;
    }
}