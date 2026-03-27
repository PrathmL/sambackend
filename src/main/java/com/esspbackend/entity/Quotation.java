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

    private Double materialCost;
    private Double laborCost;
    private Double additionalCosts;
    private Double grandTotal;

    @Column(columnDefinition = "TEXT")
    private String materialDetails; // JSON or formatted text

    @Column(name = "submitted_by_id")
    private Long submittedById;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    public Quotation() {}

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
    public Long getSubmittedById() { return submittedById; }
    public void setSubmittedById(Long submittedById) { this.submittedById = submittedById; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
