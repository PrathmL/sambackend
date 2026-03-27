package com.esspbackend.dto;

import java.time.LocalDateTime;

public class QuotationDTO {
    private Long id;
    private Long workRequestId;
    private Double materialCost;
    private Double laborCost;
    private Double additionalCosts;
    private Double grandTotal;
    private String materialDetails;
    private LocalDateTime submittedAt;

    public QuotationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
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
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}