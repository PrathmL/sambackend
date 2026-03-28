package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "punch_list_items")
public class PunchListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(nullable = false)
    private String description;

    private String location;

    private String severity; // Critical, Minor

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    private String status; // Open, Resolved

    @Column(name = "resolution_date")
    private LocalDateTime resolutionDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "photo_before")
    private String photoBefore;

    @Column(name = "photo_after")
    private String photoAfter;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "Open";
    }

    public PunchListItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(LocalDateTime resolutionDate) { this.resolutionDate = resolutionDate; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getPhotoBefore() { return photoBefore; }
    public void setPhotoBefore(String photoBefore) { this.photoBefore = photoBefore; }
    public String getPhotoAfter() { return photoAfter; }
    public void setPhotoAfter(String photoAfter) { this.photoAfter = photoAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
