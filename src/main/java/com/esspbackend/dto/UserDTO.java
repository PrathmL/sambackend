package com.esspbackend.dto;

import com.esspbackend.entity.Role;

public class UserDTO {
    private Long id;
    private String name;
    private String mobileNumber;
    private String email;
    private Role role;
    private String status;
    private Long talukaId;
    private String talukaName;
    private Long schoolId;
    private String schoolName;
    private String createdAt;
    private String lastLogin;

    public UserDTO() {}

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
    public String getTalukaName() { return talukaName; }
    public void setTalukaName(String talukaName) { this.talukaName = talukaName; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}