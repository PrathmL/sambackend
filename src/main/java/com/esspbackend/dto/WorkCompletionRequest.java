package com.esspbackend.dto;

import java.time.LocalDate;
import java.util.List;

public class WorkCompletionRequest {
    private Long workId;
    private LocalDate completionDate;
    private String finalRemarks;
    private String qualityAssessment;
    private List<String> photoUrls;

    public WorkCompletionRequest() {}

    // Getters and Setters
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    public String getFinalRemarks() { return finalRemarks; }
    public void setFinalRemarks(String finalRemarks) { this.finalRemarks = finalRemarks; }
    public String getQualityAssessment() { return qualityAssessment; }
    public void setQualityAssessment(String qualityAssessment) { this.qualityAssessment = qualityAssessment; }
    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }
}