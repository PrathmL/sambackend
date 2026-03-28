package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "communications")
public class Communication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private String type; // ANNOUNCEMENT, REMINDER, MEETING, GUIDELINE

    @Column(name = "sender_id")
    private Long senderId; // Sachiv ID

    @Column(name = "taluka_id")
    private Long talukaId; // Scoped to Sachiv's taluka

    @Column(name = "school_id")
    private Long schoolId; // Specific school ID (null if bulk to all schools in taluka)

    private Boolean isBulk;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isBulk == null) isBulk = (schoolId == null);
    }

    public Communication() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getTalukaId() { return talukaId; }
    public void setTalukaId(Long talukaId) { this.talukaId = talukaId; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public Boolean getIsBulk() { return isBulk; }
    public void setIsBulk(Boolean bulk) { isBulk = bulk; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
