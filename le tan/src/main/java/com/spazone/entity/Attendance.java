package com.spazone.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "break_start")
    private LocalDateTime breakStart;

    @Column(name = "break_end")
    private LocalDateTime breakEnd;

    @Column(name = "total_hours", precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes;

    @Column(name = "status", length = 20)
    private String status; // present, absent, late, half_day, holiday

    @Column(name = "notes", length = 4000)
    private String notes;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Attendance() {}

    // Getters and Setters
    public Integer getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId = attendanceId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public LocalDateTime getBreakStart() { return breakStart; }
    public void setBreakStart(LocalDateTime breakStart) { this.breakStart = breakStart; }

    public LocalDateTime getBreakEnd() { return breakEnd; }
    public void setBreakEnd(LocalDateTime breakEnd) { this.breakEnd = breakEnd; }

    public BigDecimal getTotalHours() { return totalHours; }
    public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public Integer getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }

    public Integer getEarlyLeaveMinutes() { return earlyLeaveMinutes; }
    public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) { this.earlyLeaveMinutes = earlyLeaveMinutes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void calculateTotalHours() {
        if (checkInTime != null && checkOutTime != null) {
            Duration totalDuration = Duration.between(checkInTime, checkOutTime);

            // Subtract break time if exists
            if (breakStart != null && breakEnd != null) {
                Duration breakDuration = Duration.between(breakStart, breakEnd);
                totalDuration = totalDuration.minus(breakDuration);
            }

            double hours = totalDuration.toMinutes() / 60.0;
            totalHours = BigDecimal.valueOf(hours);
        }
    }

    public void calculateLateMinutes(LocalTime standardStartTime) {
        if (checkInTime != null && standardStartTime != null) {
            LocalTime actualCheckIn = checkInTime.toLocalTime();
            if (actualCheckIn.isAfter(standardStartTime)) {
                lateMinutes = (int) Duration.between(standardStartTime, actualCheckIn).toMinutes();
            } else {
                lateMinutes = 0;
            }
        }
    }

    public void calculateEarlyLeaveMinutes(LocalTime standardEndTime) {
        if (checkOutTime != null && standardEndTime != null) {
            LocalTime actualCheckOut = checkOutTime.toLocalTime();
            if (actualCheckOut.isBefore(standardEndTime)) {
                earlyLeaveMinutes = (int) Duration.between(actualCheckOut, standardEndTime).toMinutes();
            } else {
                earlyLeaveMinutes = 0;
            }
        }
    }
}