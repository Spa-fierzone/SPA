package com.spazone.service.impl;

import com.spazone.dto.AttendanceSummaryDto;
import com.spazone.entity.Attendance;
import com.spazone.entity.Branch;
import com.spazone.entity.User;
import com.spazone.entity.WorkSchedule;
import com.spazone.repository.AttendanceRepository;
import com.spazone.repository.BranchRepository;
import com.spazone.repository.UserRepository;
import com.spazone.repository.WorkScheduleRepository;
import com.spazone.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Override
    public Attendance checkIn(Integer userId, Integer branchId, String location, String ipAddress) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Check if already checked in today
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today);
        if (existingAttendance.isPresent() && existingAttendance.get().getCheckInTime() != null) {
            throw new RuntimeException("Already checked in today");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Attendance attendance = existingAttendance.orElse(new Attendance());
        attendance.setUser(user);
        attendance.setBranch(branch);
        attendance.setWorkDate(today);
        attendance.setCheckInTime(now);
        attendance.setLocation(location);
        attendance.setIpAddress(ipAddress);
        attendance.setStatus("present");

        // Calculate late minutes based on work schedule
        Optional<WorkSchedule> schedule = workScheduleRepository.findByUserUserIdAndWorkDate(userId, today);
        if (schedule.isPresent()) {
            attendance.calculateLateMinutes(schedule.get().getStartTime());
        }

        // Update user's last check in
        user.setLastCheckIn(now);
        userRepository.save(user);

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseThrow(() -> new RuntimeException("No check-in record found for today"));

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out today");
        }

        attendance.setCheckOutTime(now);

        // Calculate total hours and early leave
        attendance.calculateTotalHours();

        Optional<WorkSchedule> schedule = workScheduleRepository.findByUserUserIdAndWorkDate(userId, today);
        if (schedule.isPresent()) {
            attendance.calculateEarlyLeaveMinutes(schedule.get().getEndTime());

            // Calculate overtime
            LocalTime standardEndTime = schedule.get().getEndTime();
            LocalTime actualEndTime = now.toLocalTime();
            if (actualEndTime.isAfter(standardEndTime)) {
                long overtimeMinutes = java.time.Duration.between(standardEndTime, actualEndTime).toMinutes();
                attendance.setOvertimeHours(BigDecimal.valueOf(overtimeMinutes / 60.0));
            }
        }

        // Update user's last check out
        User user = attendance.getUser();
        user.setLastCheckOut(now);
        userRepository.save(user);

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance startBreak(Integer userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseThrow(() -> new RuntimeException("No attendance record found for today"));

        if (attendance.getBreakStart() != null) {
            throw new RuntimeException("Break already started");
        }

        attendance.setBreakStart(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance endBreak(Integer userId) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByUserUserIdAndWorkDate(userId, today)
                .orElseThrow(() -> new RuntimeException("No attendance record found for today"));

        if (attendance.getBreakStart() == null) {
            throw new RuntimeException("Break not started yet");
        }

        if (attendance.getBreakEnd() != null) {
            throw new RuntimeException("Break already ended");
        }

        attendance.setBreakEnd(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> getTodayAttendance(Integer userId) {
        return attendanceRepository.findByUserUserIdAndWorkDate(userId, LocalDate.now());
    }

    @Override
    public List<Attendance> getUserAttendanceByDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByUserUserIdAndWorkDateBetween(userId, startDate, endDate);
    }

    @Override
    public Page<Attendance> getUserAttendanceHistory(Integer userId, Pageable pageable) {
        return attendanceRepository.findByUserUserIdOrderByWorkDateDesc(userId, pageable);
    }

    @Override
    public List<Attendance> getTodayAllAttendances() {
        return attendanceRepository.findByWorkDateOrderByCheckInTimeDesc(LocalDate.now());
    }

    @Override
    public List<Attendance> getBranchAttendanceByDate(Integer branchId, LocalDate date) {
        return attendanceRepository.findByBranchAndWorkDate(branchId, date);
    }

    @Override
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public Optional<Attendance> findById(Integer id) {
        return attendanceRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public Long getPresentDaysCount(Integer userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.countPresentDays(userId, startDate, endDate);
    }

    @Override
    public Long getLateDaysCount(Integer userId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.countLateDays(userId, startDate, endDate);
    }

    @Override
    public AttendanceSummaryDto getSummary(Integer userId, int month, int year) {
        List<Attendance> attendances = attendanceRepository.findByUserAndMonthYear(userId, month, year);

        long workingDays = attendances.stream().filter(a -> a.getCheckOutTime() != null).count();
        long absentDays = attendances.stream().filter(a -> a.getCheckInTime() == null).count();
        long lateDays = attendances.stream().filter(a -> a.getLateMinutes() != null && a.getLateMinutes() > 0).count();

        BigDecimal totalHours = attendances.stream()
                .map(Attendance::getTotalHours)
                .filter(h -> h != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overtimeHours = attendances.stream()
                .map(Attendance::getOvertimeHours)
                .filter(h -> h != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AttendanceSummaryDto(month, year, workingDays, absentDays, lateDays, totalHours, overtimeHours);
    }

    @Override
    public Long countWorkingDays(Integer userId, int month, int year) {
        return attendanceRepository.countByUserAndMonthAndYearAndStatus(userId, month, year, "present");
    }

    @Override
    public Long countAbsentDays(Integer userId, int month, int year) {
        return attendanceRepository.countByUserAndMonthAndYearAndStatus(userId, month, year, "absent");
    }

    @Override
    public Long countLateDays(Integer userId, int month, int year) {
        return attendanceRepository.countLateDays(userId, month, year);
    }

    @Override
    public BigDecimal sumTotalHours(Integer userId, int month, int year) {
        return attendanceRepository.sumTotalHours(userId, month, year);
    }

    @Override
    public BigDecimal sumOvertimeHours(Integer userId, int month, int year) {
        return attendanceRepository.sumOvertimeHours(userId, month, year);
    }
}