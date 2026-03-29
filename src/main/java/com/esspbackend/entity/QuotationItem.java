package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "quotation_items")
public class QuotationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quotation_id")
    private Long quotationId;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "total_price")
    private Double totalPrice;

    public QuotationItem() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getQuotationId() { return quotationId; }
    public void setQuotationId(Long quotationId) { this.quotationId = quotationId; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
}
