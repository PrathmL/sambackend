package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BlockerDTO {
    private Long id;
    private Long workId;
    private String workTitle;
    private String title;
    private String type;
    private String priority;
    private String description;
    private String impact;
    private Integer estimatedDelayDays;
    private String status;
    private String reportedBy;
    private String reportedByRole;
    private String assignedTo;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private List<BlockerCommentDTO> comments;

    public BlockerDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getWorkTitle() { return workTitle; }
    public void setWorkTitle(String workTitle) { this.workTitle = workTitle; }
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
    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }
    public String getReportedByRole() { return reportedByRole; }
    public void setReportedByRole(String reportedByRole) { this.reportedByRole = reportedByRole; }
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<BlockerCommentDTO> getComments() { return comments; }
    public void setComments(List<BlockerCommentDTO> comments) { this.comments = comments; }
}