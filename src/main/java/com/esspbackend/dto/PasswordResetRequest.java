package com.esspbackend.dto;

public class PasswordResetRequest {
    private Long userId;
    private String newPassword;

    public PasswordResetRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}