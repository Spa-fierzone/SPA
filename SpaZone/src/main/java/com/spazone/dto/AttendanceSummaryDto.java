package com.spazone.dto;

import java.math.BigDecimal;

public class AttendanceSummaryDto {
    private int month;
    private int year;
    private long workingDays;
    private long absentDays;
    private long lateDays;
    private BigDecimal totalHours;
    private BigDecimal overtimeHours;

    public AttendanceSummaryDto(int month, int year, long workingDays, long absentDays, long lateDays,
                                BigDecimal totalHours, BigDecimal overtimeHours) {
        this.month = month;
        this.year = year;
        this.workingDays = workingDays;
        this.absentDays = absentDays;
        this.lateDays = lateDays;
        this.totalHours = totalHours;
        this.overtimeHours = overtimeHours;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public long getWorkingDays() {
        return workingDays;
    }

    public long getAbsentDays() {
        return absentDays;
    }

    public long getLateDays() {
        return lateDays;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setWorkingDays(long workingDays) {
        this.workingDays = workingDays;
    }

    public void setAbsentDays(long absentDays) {
        this.absentDays = absentDays;
    }

    public void setLateDays(long lateDays) {
        this.lateDays = lateDays;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}