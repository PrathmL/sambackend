package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
public class Quotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_request_id", unique = true)
    private Long workRequestId;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "prepared_by_id")
    private Long preparedById;

    @Column(name = "material_cost")
    private Double materialCost = 0.0;

    @Column(name = "labor_cost")
    private Double laborCost = 0.0;

    @Column(name = "additional_costs")
    private Double additionalCosts = 0.0;

    @Column(name = "grand_total")
    private Double grandTotal = 0.0;

    @Column(columnDefinition = "TEXT")
    private String materialDetails;

    @Column(columnDefinition = "TEXT")
    private String laborDetails;

    @Column(columnDefinition = "TEXT")
    private String additionalDetails;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
        if (grandTotal == 0 && materialCost != null && laborCost != null && additionalCosts != null) {
            grandTotal = materialCost + laborCost + additionalCosts;
        }
        if (status == null) {
            status = "PENDING";
        }
    }

    public Quotation() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    
    public Long getPreparedById() { return preparedById; }
    public void setPreparedById(Long preparedById) { this.preparedById = preparedById; }
    
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
}