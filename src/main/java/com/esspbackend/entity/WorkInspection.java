package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_inspections")
public class WorkInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "inspector_name")
    private String inspectorName;

    private String designation;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "quality_assessment")
    private String qualityScore; // Excellent, Good, Satisfactory, Needs Improvement

    private Boolean complianceWithSpecifications;

    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (inspectionDate == null) inspectionDate = LocalDateTime.now();
    }

    public WorkInspection() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getInspectorName() { return inspectorName; }
    public void setInspectorName(String inspectorName) { this.inspectorName = inspectorName; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getQualityScore() { return qualityScore; }
    public void setQualityScore(String qualityScore) { this.qualityScore = qualityScore; }
    public Boolean getComplianceWithSpecifications() { return complianceWithSpecifications; }
    public void setComplianceWithSpecifications(Boolean complianceWithSpecifications) { this.complianceWithSpecifications = complianceWithSpecifications; }
    public LocalDateTime getInspectionDate() { return inspectionDate; }
    public void setInspectionDate(LocalDateTime inspectionDate) { this.inspectionDate = inspectionDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
