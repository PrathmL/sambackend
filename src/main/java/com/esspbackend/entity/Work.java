package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String workCode;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String type;

    @Column(name = "work_request_id")
    private Long workRequestId;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "taluka_id")
    private Long talukaId;

    @Column(name = "sanctioned_amount")
    private Double sanctionedAmount;
    
    @Column(name = "total_utilized")
    private Double totalUtilized = 0.0;
    
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;

    private String status; // DRAFT, ACTIVE, ON_HOLD, COMPLETED, PENDING_CLOSURE

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "last_update_at")
    private LocalDateTime lastUpdateAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "DRAFT";
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateAt = LocalDateTime.now();
    }

    public Work() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getWorkCode() { return workCode; }
    public void setWorkCode(String workCode) { this.workCode = workCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public Long getTalukaId() { return talukaId; }
    public void setTalukaId(Long talukaId) { this.talukaId = talukaId; }
    public Double getSanctionedAmount() { return sanctionedAmount; }
    public void setSanctionedAmount(Double sanctionedAmount) { this.sanctionedAmount = sanctionedAmount; }
    public Double getTotalUtilized() { return totalUtilized; }
    public void setTotalUtilized(Double totalUtilized) { this.totalUtilized = totalUtilized; }
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getLastUpdateAt() { return lastUpdateAt; }
    public void setLastUpdateAt(LocalDateTime lastUpdateAt) { this.lastUpdateAt = lastUpdateAt; }
}