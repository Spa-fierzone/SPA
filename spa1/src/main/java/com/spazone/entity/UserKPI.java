package com.spazone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_kpi")
public class UserKPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
    private Integer kpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "target_appointments", nullable = false)
    private Integer targetAppointments;

    @Column(name = "actual_appointments")
    private Integer actualAppointments = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "status", length = 20)
    private String status = "active"; // active, inactive

    // Constructors
    public UserKPI() {}

    public UserKPI(User technician, User manager, Integer month, Integer year, Integer targetAppointments) {
        this.technician = technician;
        this.manager = manager;
        this.month = month;
        this.year = year;
        this.targetAppointments = targetAppointments;
    }

    // Getters and Setters
    public Integer getKpiId() {
        return kpiId;
    }

    public void setKpiId(Integer kpiId) {
        this.kpiId = kpiId;
    }

    public User getTechnician() {
        return technician;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTargetAppointments() {
        return targetAppointments;
    }

    public void setTargetAppointments(Integer targetAppointments) {
        this.targetAppointments = targetAppointments;
    }

    public Integer getActualAppointments() {
        return actualAppointments;
    }

    public void setActualAppointments(Integer actualAppointments) {
        this.actualAppointments = actualAppointments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods
    public Double getCompletionPercentage() {
        if (targetAppointments == null || targetAppointments == 0) {
            return 0.0;
        }
        return (actualAppointments != null ? actualAppointments : 0) * 100.0 / targetAppointments;
    }

    public boolean isTargetAchieved() {
        return actualAppointments != null && actualAppointments >= targetAppointments;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
