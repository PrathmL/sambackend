package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_progress_updates")
public class WorkProgressUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "stage_id")
    private Long stageId;

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "material_cost")
    private Double materialCost;

    @Column(name = "labor_cost")
    private Double laborCost;

    @Column(name = "other_cost")
    private Double otherCost;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "updated_by_id")
    private Long updatedById;

    @Column(name = "updated_by_role")
    private String updatedByRole; // HEADMASTER, SACHIV, ADMIN

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
        totalCost = (materialCost != null ? materialCost : 0) + 
                    (laborCost != null ? laborCost : 0) + 
                    (otherCost != null ? otherCost : 0);
    }

    public WorkProgressUpdate() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Double getMaterialCost() { return materialCost; }
    public void setMaterialCost(Double materialCost) { this.materialCost = materialCost; }
    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }
    public Double getOtherCost() { return otherCost; }
    public void setOtherCost(Double otherCost) { this.otherCost = otherCost; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    public Long getUpdatedById() { return updatedById; }
    public void setUpdatedById(Long updatedById) { this.updatedById = updatedById; }
    public String getUpdatedByRole() { return updatedByRole; }
    public void setUpdatedByRole(String updatedByRole) { this.updatedByRole = updatedByRole; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}