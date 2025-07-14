package com.spazone.dto;

import com.spazone.enums.ShiftType;
import com.spazone.enums.ScheduleStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkScheduleDTO {
    private Integer scheduleId;
    private Integer userId;
    private String userFullName;
    private String userRole;
    private Integer branchId;
    private String branchName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftType shiftType;
    private ScheduleStatus status;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private String notes;
    private Boolean isRecurring;
    private String recurringPattern;
    private LocalDate recurringEndDate;

    // Constructors
    public WorkScheduleDTO() {}

    public WorkScheduleDTO(Integer scheduleId, Integer userId, String userFullName, String userRole,
                          Integer branchId, String branchName, LocalDate workDate, LocalTime startTime,
                          LocalTime endTime, ShiftType shiftType, ScheduleStatus status) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.userFullName = userFullName;
        this.userRole = userRole;
        this.branchId = branchId;
        this.branchName = branchName;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftType = shiftType;
        this.status = status;
    }

    // Getters and Setters
    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public ScheduleStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleStatus status) {
        this.status = status;
    }

    public LocalTime getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(LocalTime breakStart) {
        this.breakStart = breakStart;
    }

    public LocalTime getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(LocalTime breakEnd) {
        this.breakEnd = breakEnd;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public String getRecurringPattern() {
        return recurringPattern;
    }

    public void setRecurringPattern(String recurringPattern) {
        this.recurringPattern = recurringPattern;
    }

    public LocalDate getRecurringEndDate() {
        return recurringEndDate;
    }

    public void setRecurringEndDate(LocalDate recurringEndDate) {
        this.recurringEndDate = recurringEndDate;
    }

    // Helper methods
    public String getShiftTypeDisplayName() {
        return switch (shiftType) {
            case MORNING -> "Ca sáng";
            case AFTERNOON -> "Ca chiều";
            case EVENING -> "Ca tối";
            case NIGHT -> "Ca đêm";
            case FULL_DAY -> "Cả ngày";
            default -> shiftType.name();
        };
    }

    public String getStatusDisplayName() {
        return switch (status) {
            case SCHEDULED -> "Đã lên lịch";
            case COMPLETED -> "Hoàn thành";
            case CANCELLED -> "Đã hủy";
            case RESCHEDULED -> "Đã thay đổi lịch";
            case NO_SHOW -> "Không có mặt";
            default -> status.name();
        };
    }

    public long getScheduledHours() {
        if (startTime != null && endTime != null) {
            long hours = java.time.Duration.between(startTime, endTime).toHours();

            // Subtract break time if available
            if (breakStart != null && breakEnd != null) {
                long breakHours = java.time.Duration.between(breakStart, breakEnd).toHours();
                hours -= breakHours;
            }

            return Math.max(0, hours); // Ensure non-negative
        }
        return 0;
    }
}
