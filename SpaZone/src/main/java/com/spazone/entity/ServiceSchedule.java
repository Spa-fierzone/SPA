package com.spazone.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "service_schedule")
public class ServiceSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "serviceId")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "branchId")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "technician_id", referencedColumnName = "user_id")
    private User technician;

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ServiceSchedule() {
    }

    public ServiceSchedule(Service service, Branch branch, User technician, LocalTime startTime, LocalTime endTime, Integer dayOfWeek, Boolean isActive) {
        this.service = service;
        this.branch = branch;
        this.technician = technician;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.isActive = isActive;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public Service getService() {
        return service;
    }

    public Branch getBranch() {
        return branch;
    }

    public User getTechnician() {
        return technician;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}