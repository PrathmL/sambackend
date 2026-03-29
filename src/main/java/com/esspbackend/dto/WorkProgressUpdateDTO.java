package com.esspbackend.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.esspbackend.entity.WorkProgressItemUsage;

public class WorkProgressUpdateDTO {
    private Long id;
    private Long workId;
    private Long stageId;
    private String stageName;
    private Integer progressPercentage;
    private String remarks;
    private Double materialCost;
    private Double laborCost;
    private Double otherCost;
    private Double totalCost;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<PhotoDTO> photoUrls;
    private List<WorkProgressItemUsage> itemUsage;

    public WorkProgressUpdateDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    
    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public Double getMaterialCost() { return materialCost; }
    public void setMaterialCost(Double materialCost) { this.materialCost = materialCost; }
    
    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }
    
    public Double getOtherCost() { return otherCost; }
    public void setOtherCost(Double otherCost) { this.otherCost = otherCost; }
    
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<PhotoDTO> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<PhotoDTO> photoUrls) { this.photoUrls = photoUrls; }

    public List<WorkProgressItemUsage> getItemUsage() { return itemUsage; }
    public void setItemUsage(List<WorkProgressItemUsage> itemUsage) { this.itemUsage = itemUsage; }
}