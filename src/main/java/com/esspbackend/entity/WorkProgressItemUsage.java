package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_progress_item_usage")
public class WorkProgressItemUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_progress_update_id")
    private Long workProgressUpdateId;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "quantity_used")
    private Double quantityUsed;

    public WorkProgressItemUsage() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkProgressUpdateId() { return workProgressUpdateId; }
    public void setWorkProgressUpdateId(Long workProgressUpdateId) { this.workProgressUpdateId = workProgressUpdateId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public Double getQuantityUsed() { return quantityUsed; }
    public void setQuantityUsed(Double quantityUsed) { this.quantityUsed = quantityUsed; }
}
