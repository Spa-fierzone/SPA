package com.spazone.service.impl;

import com.spazone.dto.SalaryCalculationDto;
import com.spazone.entity.*;
import com.spazone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalaryService {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentHandledRepository appointmentHandledRepository;

    @Autowired
    private SalarySettingRepository salarySettingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    private static final BigDecimal DEFAULT_BASE_SALARY = new BigDecimal("5000000");
    private static final BigDecimal OVERTIME_RATE = new BigDecimal("1.5");
    private static final int STANDARD_WORKING_DAYS = 22;
    private static final double STANDARD_WORKING_HOURS = 8.0;

    public List<SalaryCalculationDto> calculateMonthlySalary(Integer month, Integer year, Integer branchId) {
        List<SalaryCalculationDto> calculations = new ArrayList<>();
        List<User> users = branchId != null ? userRepository.findActiveUsersByBranch(branchId) : userRepository.findUsersByMainRoles();

        for (User user : users) {
            SalaryCalculationDto calculation = calculateUserSalary(user, month, year);
            calculations.add(calculation);
        }
        return calculations;
    }

    public SalaryCalculationDto calculateUserSalary(User user, Integer month, Integer year) {
        SalaryCalculationDto dto = new SalaryCalculationDto();
        dto.setUser(user);
        dto.setMonth(month);
        dto.setYear(year);

        List<Attendance> attendances = attendanceRepository.findByUserAndMonthYear(user.getUserId(), month, year);
        List<WorkSchedule> schedules = workScheduleRepository.findByUserAndMonthYear(user.getUserId(), month, year);

        long workingDays = attendances.stream().mapToLong(a -> "present".equals(a.getStatus()) ? 1 : 0).sum();
        double totalHours = attendances.stream().filter(a -> a.getTotalHours() != null).mapToDouble(a -> a.getTotalHours().doubleValue()).sum();
        double overtimeHours = attendances.stream().filter(a -> a.getOvertimeHours() != null).mapToDouble(a -> a.getOvertimeHours().doubleValue()).sum();
        long lateDays = attendances.stream().mapToLong(a -> (a.getLateMinutes() != null && a.getLateMinutes() > 0) ? 1 : 0).sum();
        long absentDays = schedules.size() - workingDays;

        dto.setScheduledDays((long) schedules.size());
        dto.setWorkingDays(workingDays);
        dto.setAbsentDays(absentDays);
        dto.setLateDays(lateDays);
        dto.setTotalHours(BigDecimal.valueOf(totalHours));
        dto.setOvertimeHours(BigDecimal.valueOf(overtimeHours));

        BigDecimal baseSalary = user.getBaseSalary() != null ? user.getBaseSalary() : DEFAULT_BASE_SALARY;
        dto.setBaseSalary(baseSalary);

        int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
        BigDecimal hourlyRate = baseSalary
                .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP);

        dto.setHourlyRate(hourlyRate);

        BigDecimal actualSalary = hourlyRate.multiply(BigDecimal.valueOf(totalHours));
        dto.setActualSalary(actualSalary);

        BigDecimal overtimePay = hourlyRate.multiply(OVERTIME_RATE).multiply(BigDecimal.valueOf(overtimeHours));
        dto.setOvertimePay(overtimePay);

        Role mainRole = user.getRoles().stream().findFirst().orElse(null);
        SalarySetting.Role settingRole = SalarySetting.Role.valueOf(mainRole.getRoleName());
        SalarySetting setting = salarySettingRepository.findByRole(settingRole).orElse(null);

        BigDecimal commission = BigDecimal.ZERO;
        if (setting != null) {
            if (user.getRoles().stream().anyMatch(r -> r.getRoleName().equals("RECEPTIONIST")))  {
                List<AppointmentHandled> handledList = appointmentHandledRepository.findByReceptionistAndMonth(user.getUserId(), month, year);
                commission = handledList.stream()
                        .map(AppointmentHandled::getAppointment)
                        .filter(app -> app.getService() != null && app.getService().getPrice() != null)
                        .map(app -> app.getService().getPrice()
                                .multiply(BigDecimal.valueOf(setting.getCommissionPerCustomer() / 100.0)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } else if (user.getRoles().stream().anyMatch(r -> r.getRoleName().equals("TECHNICIAN"))) {
                LocalDateTime start = LocalDate.of(year, month, 1).atStartOfDay();
                LocalDateTime end = start.plusMonths(1).minusSeconds(1);
                List<Appointment> techAppointments = appointmentRepository
                        .findByTechnicianUserIdAndAppointmentDateBetween(user.getUserId(), start, end);

                commission = techAppointments.stream()
                        .filter(app -> app.getService() != null && app.getService().getPrice() != null)
                        .map(app -> app.getService().getPrice()
                                .multiply(BigDecimal.valueOf(setting.getCommissionPerCustomer() / 100.0)))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }

        BigDecimal dailySalary = hourlyRate.multiply(BigDecimal.valueOf(8)); // lương theo ngày
        BigDecimal lateDeduction = dailySalary.multiply(BigDecimal.valueOf(0.1)).multiply(BigDecimal.valueOf(lateDays));
        BigDecimal absentDeduction = dailySalary.multiply(BigDecimal.valueOf(absentDays));
        BigDecimal totalDeductions = lateDeduction.add(absentDeduction);
        dto.setDeductions(totalDeductions);

        BigDecimal netSalary = actualSalary.add(overtimePay).add(commission).subtract(totalDeductions);
        dto.setNetSalary(netSalary);
        dto.setCommission(commission);

        Optional<SalaryRecord> existingRecord = salaryRecordRepository.findByUserAndMonthYear(user.getUserId(), month, year);
        dto.setHasSalaryRecord(existingRecord.isPresent());
        existingRecord.ifPresent(dto::setExistingSalaryRecord);

        return dto;
    }


    public void saveSalaryRecord(SalaryCalculationDto calculation, Integer branchId) {
        SalaryRecord record = new SalaryRecord();
        record.setUser(calculation.getUser());

        Branch branch = null;
        if (branchId != null) {
            branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi nhánh ID: " + branchId));
        } else if (calculation.getUser() != null && calculation.getUser().getBranch() != null) {
            branch = calculation.getUser().getBranch();
            if (branch.getBranchId() != null) {
                branch = branchRepository.findById(branch.getBranchId()).orElse(null);
            }
        }

        if (branch == null) {
            throw new IllegalArgumentException("Không thể xác định chi nhánh cho nhân viên: "
                    + (calculation.getUser() != null ? calculation.getUser().getFullName() : "UNKNOWN"));
        }

        record.setBranch(branch);
        record.setMonth(calculation.getMonth());
        record.setYear(calculation.getYear());
        record.setBaseSalary(calculation.getBaseSalary());
        record.setOvertimePay(calculation.getOvertimePay());
        record.setDeductions(calculation.getDeductions());
        record.setNetSalary(calculation.getNetSalary());
        record.setCommission(calculation.getCommission());
        record.setStatus("approved");
        record.setNotes("Tính lương tự động dựa trên chấm công và cấu hình hoa hồng");

        salaryRecordRepository.save(record);
    }

    public void saveBatchSalaryRecords(List<SalaryCalculationDto> calculations, Integer branchId) {
        for (SalaryCalculationDto calculation : calculations) {
            if (!calculation.isHasSalaryRecord()) {
                saveSalaryRecord(calculation, branchId);
            }
        }
    }

    public List<SalaryRecord> getSalaryRecords(Integer month, Integer year, Integer branchId) {
        if (branchId != null) {
            return salaryRecordRepository.findByMonthYearAndBranch(month, year, branchId);
        }
        return salaryRecordRepository.findByMonthYear(month, year);
    }
}
