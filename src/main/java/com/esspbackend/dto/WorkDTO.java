package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkDTO {
    private Long id;
    private String workCode;
    private String title;
    private String description;
    private String type;
    private Double sanctionedAmount;
    private Double totalUtilized;
    private Integer progressPercentage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastUpdateAt;
    private List<WorkStageDTO> stages;
    private List<WorkProgressUpdateDTO> progressUpdates;

    public WorkDTO() {}

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
    
    public List<WorkStageDTO> getStages() { return stages; }
    public void setStages(List<WorkStageDTO> stages) { this.stages = stages; }
    
    public List<WorkProgressUpdateDTO> getProgressUpdates() { return progressUpdates; }
    public void setProgressUpdates(List<WorkProgressUpdateDTO> progressUpdates) { this.progressUpdates = progressUpdates; }
}