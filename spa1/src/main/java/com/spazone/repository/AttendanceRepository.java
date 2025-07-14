package com.spazone.repository;

import com.spazone.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    Optional<Attendance> findByUserUserIdAndWorkDate(Integer userId, LocalDate workDate);

    List<Attendance> findByUserUserIdAndWorkDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    Page<Attendance> findByUserUserIdOrderByWorkDateDesc(Integer userId, Pageable pageable);

    List<Attendance> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.workDate = :date ORDER BY a.checkInTime DESC")
    List<Attendance> findByWorkDateOrderByCheckInTimeDesc(@Param("date") LocalDate date);

    @Query("SELECT a FROM Attendance a WHERE a.branch.branchId = :branchId AND a.workDate = :date")
    List<Attendance> findByBranchAndWorkDate(@Param("branchId") Integer branchId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND a.workDate BETWEEN :startDate AND :endDate AND a.status = 'present'")
    Long countPresentDays(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND a.workDate BETWEEN :startDate AND :endDate AND a.lateMinutes > 0")
    Long countLateDays(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    List<Attendance> findByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT a FROM Attendance a WHERE MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    List<Attendance> findByMonthYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND a.status = 'present' AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    Long countWorkingDaysByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(a.totalHours) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    Double sumTotalHoursByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    Double sumOvertimeHoursByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    // Additional methods for salary calculations
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year AND a.status = :status")
    Long countByUserAndMonthAndYearAndStatus(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year, @Param("status") String status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year AND a.lateMinutes > 0")
    Long countLateDays(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(a.totalHours) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    BigDecimal sumTotalHours(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(a.overtimeHours) FROM Attendance a WHERE a.user.userId = :userId AND MONTH(a.workDate) = :month AND YEAR(a.workDate) = :year")
    BigDecimal sumOvertimeHours(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);
}