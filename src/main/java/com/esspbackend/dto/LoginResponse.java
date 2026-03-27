package com.esspbackend.dto;

import com.esspbackend.entity.Role;

public class LoginResponse {
    private Long id;
    private String name;
    private String mobileNumber;
    private String email;
    private Role role;
    private String status;
    private Long talukaId;
    private Long schoolId;

    public LoginResponse() {}

    public LoginResponse(Long id, String name, String mobileNumber, String email, Role role, String status, Long talukaId, Long schoolId) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.role = role;
        this.status = status;
        this.talukaId = talukaId;
        this.schoolId = schoolId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getTalukaId() { return talukaId; }
    public void setTalukaId(Long talukaId) { this.talukaId = talukaId; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}