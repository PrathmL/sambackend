package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.esspbackend.entity.QuotationItem;

public class QuotationDTO {
    private Long id;
    private Long workRequestId;
    private Long schoolId;
    private String quotationType;
    private Double materialCost;
    private Double laborCost;
    private Double additionalCosts;
    private Double grandTotal;
    private String materialDetails;
    private String laborDetails;
    private String additionalDetails;
    private LocalDateTime validUntil;
    private String status; // PENDING, APPROVED, REJECTED
    private String adminRemarks;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private List<QuotationItem> items;

    public QuotationDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }

    public String getQuotationType() { return quotationType; }
    public void setQuotationType(String quotationType) { this.quotationType = quotationType; }
    
    public Double getMaterialCost() { return materialCost; }
    public void setMaterialCost(Double materialCost) { this.materialCost = materialCost; }
    
    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }
    
    public Double getAdditionalCosts() { return additionalCosts; }
    public void setAdditionalCosts(Double additionalCosts) { this.additionalCosts = additionalCosts; }
    
    public Double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(Double grandTotal) { this.grandTotal = grandTotal; }
    
    public String getMaterialDetails() { return materialDetails; }
    public void setMaterialDetails(String materialDetails) { this.materialDetails = materialDetails; }
    
    public String getLaborDetails() { return laborDetails; }
    public void setLaborDetails(String laborDetails) { this.laborDetails = laborDetails; }
    
    public String getAdditionalDetails() { return additionalDetails; }
    public void setAdditionalDetails(String additionalDetails) { this.additionalDetails = additionalDetails; }
    
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }

    public List<QuotationItem> getItems() { return items; }
    public void setItems(List<QuotationItem> items) { this.items = items; }
}