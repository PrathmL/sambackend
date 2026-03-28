package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BlockerDTO {
    private Long id;
    private Long workId;
    private String workTitle;
    private String workCode;
    private String title;
    private String type;
    private String priority;
    private String description;
    private String impact;
    private Integer estimatedDelayDays;
    private String status;
    private Long schoolId;
    private String schoolName;
    private String reportedBy;
    private String reportedByRole;
    private LocalDateTime reportedAt;
    private Long assignedToId;
    private String assignedTo;
    private String assignedToRole;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime escalatedAt;
    private String escalatedTo;
    private String escalationReason;
    private Long duplicateOfId;
    private String duplicateOfTitle;
    private LocalDateTime targetDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<BlockerCommentDTO> comments;

    public BlockerDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    
    public String getWorkTitle() { return workTitle; }
    public void setWorkTitle(String workTitle) { this.workTitle = workTitle; }
    
    public String getWorkCode() { return workCode; }
    public void setWorkCode(String workCode) { this.workCode = workCode; }
    
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
    
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    
    public String getReportedByRole() { return reportedByRole; }
    public void setReportedByRole(String reportedByRole) { this.reportedByRole = reportedByRole; }
    
    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
    
    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public String getAssignedToRole() { return assignedToRole; }
    public void setAssignedToRole(String assignedToRole) { this.assignedToRole = assignedToRole; }
    
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    
    public LocalDateTime getEscalatedAt() { return escalatedAt; }
    public void setEscalatedAt(LocalDateTime escalatedAt) { this.escalatedAt = escalatedAt; }
    
    public String getEscalatedTo() { return escalatedTo; }
    public void setEscalatedTo(String escalatedTo) { this.escalatedTo = escalatedTo; }
    
    public String getEscalationReason() { return escalationReason; }
    public void setEscalationReason(String escalationReason) { this.escalationReason = escalationReason; }
    
    public Long getDuplicateOfId() { return duplicateOfId; }
    public void setDuplicateOfId(Long duplicateOfId) { this.duplicateOfId = duplicateOfId; }
    
    public String getDuplicateOfTitle() { return duplicateOfTitle; }
    public void setDuplicateOfTitle(String duplicateOfTitle) { this.duplicateOfTitle = duplicateOfTitle; }
    
    public LocalDateTime getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDateTime targetDate) { this.targetDate = targetDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<BlockerCommentDTO> getComments() { return comments; }
    public void setComments(List<BlockerCommentDTO> comments) { this.comments = comments; }
}