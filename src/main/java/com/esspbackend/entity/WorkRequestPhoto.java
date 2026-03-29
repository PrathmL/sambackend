package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_request_photos")
public class WorkRequestPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_request_id")
    private Long workRequestId;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    private String caption;

    @Column(name = "order_index")
    private Integer orderIndex;

    private Double latitude;
    private Double longitude;

    public WorkRequestPhoto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkRequestId() { return workRequestId; }
    public void setWorkRequestId(Long workRequestId) { this.workRequestId = workRequestId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}