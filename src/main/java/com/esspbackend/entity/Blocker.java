package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blockers")
public class Blocker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id")
    private Long workId;

    private String title;

    private String type; // Material Shortage, Labor Availability, Fund Issues, Administrative Delay, Technical Issue, Weather/Seasonal, Contractor Issue, Quality Issue, Safety Concern, Other

    private String priority; // HIGH, MEDIUM, LOW

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String impact;

    @Column(name = "estimated_delay_days")
    private Integer estimatedDelayDays;

    private String status; // NEW, IN_PROGRESS, RESOLVED, ESCALATED, REJECTED

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "reported_by_id")
    private Long reportedById;

    @Column(name = "reported_by_role")
    private String reportedByRole; // HEADMASTER, SACHIV, ADMIN, CLERK

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(name = "assigned_to_role")
    private String assignedToRole;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @Column(name = "escalated_to_id")
    private Long escalatedToId;

    @Column(name = "escalated_to_role")
    private String escalatedToRole;

    @Column(name = "escalation_reason", columnDefinition = "TEXT")
    private String escalationReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "NEW";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Blocker() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
    
    public Integer getEstimatedDelayDays() { return estimatedDelayDays; }
    public void setEstimatedDelayDays(Integer estimatedDelayDays) { this.estimatedDelayDays = estimatedDelayDays; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    
    public Long getReportedById() { return reportedById; }
    public void setReportedById(Long reportedById) { this.reportedById = reportedById; }
    
    public String getReportedByRole() { return reportedByRole; }
    public void setReportedByRole(String reportedByRole) { this.reportedByRole = reportedByRole; }
    
    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    
    public String getAssignedToRole() { return assignedToRole; }
    public void setAssignedToRole(String assignedToRole) { this.assignedToRole = assignedToRole; }
    
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }
    
    public Long getEscalatedToId() { return escalatedToId; }
    public void setEscalatedToId(Long escalatedToId) { this.escalatedToId = escalatedToId; }
    
    public String getEscalatedToRole() { return escalatedToRole; }
    public void setEscalatedToRole(String escalatedToRole) { this.escalatedToRole = escalatedToRole; }
    
    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}