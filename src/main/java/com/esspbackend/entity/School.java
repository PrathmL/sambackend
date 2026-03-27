package com.esspbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "taluka_id")
    private Long talukaId;

    private String address;

    private String contactDetails;

    private Integer establishedYear;

    private String status; // Active/Inactive

    public School() {}

    public School(Long id, String name, String code, Long talukaId, String address, 
                  String contactDetails, Integer establishedYear, String status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.talukaId = talukaId;
        this.address = address;
        this.contactDetails = contactDetails;
        this.establishedYear = establishedYear;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getTalukaId() { return talukaId; }
    public void setTalukaId(Long talukaId) { this.talukaId = talukaId; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactDetails() { return contactDetails; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
    public Integer getEstablishedYear() { return establishedYear; }
    public void setEstablishedYear(Integer establishedYear) { this.establishedYear = establishedYear; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}