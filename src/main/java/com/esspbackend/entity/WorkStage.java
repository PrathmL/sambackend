package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_stages")
public class WorkStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id")
    private Long workId;

    private String name;
    
    private String description;
    
    private Integer weightage; // e.g., 20 for 20%
    
    @Column(name = "estimated_duration_days")
    private Integer estimatedDurationDays;
    
    @Column(name = "actual_duration_days")
    private Integer actualDurationDays;
    
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;
    
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "expected_completion_date")
    private LocalDateTime expectedCompletionDate;
    
    @Column(name = "actual_completion_date")
    private LocalDateTime actualCompletionDate;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;

    public WorkStage() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getWeightage() { return weightage; }
    public void setWeightage(Integer weightage) { this.weightage = weightage; }
    public Integer getEstimatedDurationDays() { return estimatedDurationDays; }
    public void setEstimatedDurationDays(Integer estimatedDurationDays) { this.estimatedDurationDays = estimatedDurationDays; }
    public Integer getActualDurationDays() { return actualDurationDays; }
    public void setActualDurationDays(Integer actualDurationDays) { this.actualDurationDays = actualDurationDays; }
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getExpectedCompletionDate() { return expectedCompletionDate; }
    public void setExpectedCompletionDate(LocalDateTime expectedCompletionDate) { this.expectedCompletionDate = expectedCompletionDate; }
    public LocalDateTime getActualCompletionDate() { return actualCompletionDate; }
    public void setActualCompletionDate(LocalDateTime actualCompletionDate) { this.actualCompletionDate = actualCompletionDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}