package com.esspbackend.dto;

import java.util.Map;

public class BlockerStatsDTO {
    private long totalBlockers;
    private long newBlockers;
    private long inProgressBlockers;
    private long resolvedBlockers;
    private long escalatedBlockers;
    private long highPriorityBlockers;
    private double averageResolutionTimeDays;
    private Map<String, Long> blockersByType;
    private Map<String, Long> blockersByPriority;
    private Map<String, Long> blockersByStatus;
    private Map<String, Long> blockersByTaluka;

    public BlockerStatsDTO() {}

    // Getters and Setters
    public long getTotalBlockers() { return totalBlockers; }
    public void setTotalBlockers(long totalBlockers) { this.totalBlockers = totalBlockers; }
    
    public long getNewBlockers() { return newBlockers; }
    public void setNewBlockers(long newBlockers) { this.newBlockers = newBlockers; }
    
    public long getInProgressBlockers() { return inProgressBlockers; }
    public void setInProgressBlockers(long inProgressBlockers) { this.inProgressBlockers = inProgressBlockers; }
    
    public long getResolvedBlockers() { return resolvedBlockers; }
    public void setResolvedBlockers(long resolvedBlockers) { this.resolvedBlockers = resolvedBlockers; }
    
    public long getEscalatedBlockers() { return escalatedBlockers; }
    public void setEscalatedBlockers(long escalatedBlockers) { this.escalatedBlockers = escalatedBlockers; }
    
    public long getHighPriorityBlockers() { return highPriorityBlockers; }
    public void setHighPriorityBlockers(long highPriorityBlockers) { this.highPriorityBlockers = highPriorityBlockers; }
    
    public double getAverageResolutionTimeDays() { return averageResolutionTimeDays; }
    public void setAverageResolutionTimeDays(double averageResolutionTimeDays) { this.averageResolutionTimeDays = averageResolutionTimeDays; }
    
    public Map<String, Long> getBlockersByType() { return blockersByType; }
    public void setBlockersByType(Map<String, Long> blockersByType) { this.blockersByType = blockersByType; }
    
    public Map<String, Long> getBlockersByPriority() { return blockersByPriority; }
    public void setBlockersByPriority(Map<String, Long> blockersByPriority) { this.blockersByPriority = blockersByPriority; }
    
    public Map<String, Long> getBlockersByStatus() { return blockersByStatus; }
    public void setBlockersByStatus(Map<String, Long> blockersByStatus) { this.blockersByStatus = blockersByStatus; }
    
    public Map<String, Long> getBlockersByTaluka() { return blockersByTaluka; }
    public void setBlockersByTaluka(Map<String, Long> blockersByTaluka) { this.blockersByTaluka = blockersByTaluka; }
}