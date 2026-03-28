package com.esspbackend.dto;

public class InventoryStatsDTO {
    private long totalMaterials;
    private long lowStockItems;
    private long outOfStockItems;
    private double totalInventoryValue;
    private double monthlyConsumption;
    private long pendingQuotations;

    public InventoryStatsDTO() {}

    // Getters and Setters
    public long getTotalMaterials() { return totalMaterials; }
    public void setTotalMaterials(long totalMaterials) { this.totalMaterials = totalMaterials; }
    public long getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(long lowStockItems) { this.lowStockItems = lowStockItems; }
    public long getOutOfStockItems() { return outOfStockItems; }
    public void setOutOfStockItems(long outOfStockItems) { this.outOfStockItems = outOfStockItems; }
    public double getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(double totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    public double getMonthlyConsumption() { return monthlyConsumption; }
    public void setMonthlyConsumption(double monthlyConsumption) { this.monthlyConsumption = monthlyConsumption; }
    public long getPendingQuotations() { return pendingQuotations; }
    public void setPendingQuotations(long pendingQuotations) { this.pendingQuotations = pendingQuotations; }
}