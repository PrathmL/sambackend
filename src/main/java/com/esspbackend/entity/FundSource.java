package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fund_sources")
public class FundSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id")
    private Long workId;

    private String sourceName; // e.g., Government Grant, School Fund, Donation
    private Double amount;

    public FundSource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
