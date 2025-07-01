package com.spazone.dto;

import com.spazone.entity.SalaryRecord;
import com.spazone.entity.User;

import java.math.BigDecimal;

public class SalaryCalculationDto {
    private User user;
    private Integer month;
    private Integer year;
    private Long scheduledDays;
    private Long workingDays;
    private Long absentDays;
    private Long lateDays;
    private BigDecimal totalHours;
    private BigDecimal overtimeHours;
    private BigDecimal baseSalary;
    private BigDecimal actualSalary;
    private BigDecimal overtimePay;
    private BigDecimal bonus = BigDecimal.ZERO;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private boolean hasSalaryRecord;
    private SalaryRecord existingSalaryRecord;
    private BigDecimal commission;
    private BigDecimal hourlyRate;

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    // Constructors
    public SalaryCalculationDto() {}

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Long getScheduledDays() {
        return scheduledDays;
    }

    public void setScheduledDays(Long scheduledDays) {
        this.scheduledDays = scheduledDays;
    }

    public Long getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(Long workingDays) {
        this.workingDays = workingDays;
    }

    public Long getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(Long absentDays) {
        this.absentDays = absentDays;
    }

    public Long getLateDays() {
        return lateDays;
    }

    public void setLateDays(Long lateDays) {
        this.lateDays = lateDays;
    }

    public BigDecimal getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getActualSalary() {
        return actualSalary;
    }

    public void setActualSalary(BigDecimal actualSalary) {
        this.actualSalary = actualSalary;
    }

    public BigDecimal getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(BigDecimal overtimePay) {
        this.overtimePay = overtimePay;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getDeductions() {
        return deductions;
    }

    public void setDeductions(BigDecimal deductions) {
        this.deductions = deductions;
    }

    public BigDecimal getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(BigDecimal netSalary) {
        this.netSalary = netSalary;
    }

    public boolean isHasSalaryRecord() {
        return hasSalaryRecord;
    }

    public void setHasSalaryRecord(boolean hasSalaryRecord) {
        this.hasSalaryRecord = hasSalaryRecord;
    }

    public SalaryRecord getExistingSalaryRecord() {
        return existingSalaryRecord;
    }

    public void setExistingSalaryRecord(SalaryRecord existingSalaryRecord) {
        this.existingSalaryRecord = existingSalaryRecord;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    // Additional getter methods for template compatibility
    public BigDecimal getTotalSalary() {
        // Return netSalary as totalSalary for template compatibility
        return netSalary != null ? netSalary : BigDecimal.ZERO;
    }

    public BigDecimal getAllowance() {
        // Calculate allowance = actualSalary - baseSalary (if needed)
        // Or return a default value if you have a separate allowance field
        if (actualSalary != null && baseSalary != null) {
            BigDecimal diff = actualSalary.subtract(baseSalary);
            return diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDeduction() {
        // Return deductions with singular name for template compatibility
        return deductions != null ? deductions : BigDecimal.ZERO;
    }

    // Helper methods
    public double getAttendanceRate() {
        if (scheduledDays == null || scheduledDays == 0) return 0;
        return (workingDays.doubleValue() / scheduledDays.doubleValue()) * 100;
    }

    public String getAttendanceRateFormatted() {
        return String.format("%.1f%%", getAttendanceRate());
    }
}