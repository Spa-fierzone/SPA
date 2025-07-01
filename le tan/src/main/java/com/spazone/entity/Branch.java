package com.spazone.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer branchId;

    private String name;
    private String address;
    private String phone;
    private String email;
    private String openingHours;
    private String operatingHours;
    private String holidaySchedule;
    private Integer capacity;
    private Integer managerId;

    private String status = "active";  // active, inactive, suspended, maintenance
    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Branch() {
    }

    public Branch(Integer branchId, String name, String address, String phone, String email, String openingHours, String operatingHours, String holidaySchedule, Integer capacity, Integer managerId, String status, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.branchId = branchId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.openingHours = openingHours;
        this.operatingHours = operatingHours;
        this.holidaySchedule = holidaySchedule;
        this.capacity = capacity;
        this.managerId = managerId;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public String getHolidaySchedule() {
        return holidaySchedule;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public void setHolidaySchedule(String holidaySchedule) {
        this.holidaySchedule = holidaySchedule;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

