package com.esspbackend.dto;

import java.util.List;

public class WorkCreationRequest {
    private Long workRequestId;
    private String workCode;
    private String title;
    private String description;
    private String type;
    private Double sanctionedAmount;
    private List<StageDTO> stages;
    private List<FundSourceDTO> fundSources;

    public WorkCreationRequest() {}

    // Getters and Setters
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
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
    public List<StageDTO> getStages() { return stages; }
    public void setStages(List<StageDTO> stages) { this.stages = stages; }
    public List<FundSourceDTO> getFundSources() { return fundSources; }
    public void setFundSources(List<FundSourceDTO> fundSources) { this.fundSources = fundSources; }

    public static class StageDTO {
        private String name;
        private String description;
        private Integer weightage;
        private Integer estimatedDurationDays;

        public StageDTO() {}

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getWeightage() { return weightage; }
        public void setWeightage(Integer weightage) { this.weightage = weightage; }
        public Integer getEstimatedDurationDays() { return estimatedDurationDays; }
        public void setEstimatedDurationDays(Integer estimatedDurationDays) { this.estimatedDurationDays = estimatedDurationDays; }
    }

    public static class FundSourceDTO {
        private String sourceName;
        private Double amount;

        public FundSourceDTO() {}

        public String getSourceName() { return sourceName; }
        public void setSourceName(String sourceName) { this.sourceName = sourceName; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }
}