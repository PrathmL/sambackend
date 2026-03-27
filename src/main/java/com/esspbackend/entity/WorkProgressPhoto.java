package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "work_progress_photos")
public class WorkProgressPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "progress_update_id")
    private Long progressUpdateId;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    private String caption;

    @Column(name = "geo_location")
    private String geoLocation;

    public WorkProgressPhoto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProgressUpdateId() { return progressUpdateId; }
    public void setProgressUpdateId(Long progressUpdateId) { this.progressUpdateId = progressUpdateId; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }
}