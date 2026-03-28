package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action; // LOGIN, USER_CREATION, WORK_APPROVAL, SETTINGS_CHANGE, etc.

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "performed_by_id")
    private Long performedById;

    @Column(name = "performed_by_name")
    private String performedByName;

    @Column(name = "performed_by_role")
    private String performedByRole;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public AuditLog() {}

    public AuditLog(String action, String details, Long performedById, String performedByName, String performedByRole) {
        this.action = action;
        this.details = details;
        this.performedById = performedById;
        this.performedByName = performedByName;
        this.performedByRole = performedByRole;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Long getPerformedById() { return performedById; }
    public void setPerformedById(Long performedById) { this.performedById = performedById; }
    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }
    public String getPerformedByRole() { return performedByRole; }
    public void setPerformedByRole(String performedByRole) { this.performedByRole = performedByRole; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
