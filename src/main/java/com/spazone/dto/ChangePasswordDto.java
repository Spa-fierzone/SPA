package com.spazone.dto;

public class ChangePasswordDto {
    private String currentPassword;
    private String newPassword;

    // Getters và setters
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
