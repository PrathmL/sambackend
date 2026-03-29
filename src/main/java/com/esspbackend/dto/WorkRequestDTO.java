package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkRequestDTO {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String category;
    private String priority;
    private Long schoolId;
    private String schoolName;
    private String status;
    private String rejectionReason;
    private String adminRemarks;
    private String expectedTimeline;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime createdAt;
    private List<PhotoDTO> photoUrls;
    private QuotationDTO quotation;

    // Constructors
    public WorkRequestDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
    public String getExpectedTimeline() { return expectedTimeline; }
    public void setExpectedTimeline(String expectedTimeline) { this.expectedTimeline = expectedTimeline; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<PhotoDTO> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<PhotoDTO> photoUrls) { this.photoUrls = photoUrls; }
    public QuotationDTO getQuotation() { return quotation; }
    public void setQuotation(QuotationDTO quotation) { this.quotation = quotation; }
}