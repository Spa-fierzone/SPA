package com.spazone.service;

import com.spazone.dto.AttendanceSummaryDto;
import com.spazone.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceService {
    Attendance checkIn(Integer userId, Integer branchId, String location, String ipAddress);

    Attendance checkOut(Integer userId);

    Attendance startBreak(Integer userId);

    Attendance endBreak(Integer userId);

    Optional<Attendance> getTodayAttendance(Integer userId);

    List<Attendance> getUserAttendanceByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    Page<Attendance> getUserAttendanceHistory(Integer userId, Pageable pageable);

    List<Attendance> getTodayAllAttendances();

    List<Attendance> getBranchAttendanceByDate(Integer branchId, LocalDate date);

    Attendance save(Attendance attendance);

    Optional<Attendance> findById(Integer id);

    void deleteById(Integer id);

    Long getPresentDaysCount(Integer userId, LocalDate startDate, LocalDate endDate);

    Long getLateDaysCount(Integer userId, LocalDate startDate, LocalDate endDate);

    AttendanceSummaryDto getSummary(Integer userId, int month, int year);

    Long countWorkingDays(Integer userId, int month, int year);

    Long countAbsentDays(Integer userId, int month, int year);

    public Long countLateDays(Integer userId, int month, int year);

    BigDecimal sumTotalHours(Integer userId, int month, int year);

    BigDecimal sumOvertimeHours(Integer userId, int month, int year);
}
