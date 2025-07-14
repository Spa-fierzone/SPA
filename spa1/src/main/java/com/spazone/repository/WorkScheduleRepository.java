package com.spazone.repository;

import com.spazone.entity.WorkSchedule;
import com.spazone.enums.ScheduleStatus;
import com.spazone.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {

    // Find schedules by user ID
    List<WorkSchedule> findByUserUserId(Integer userId);

    // Find schedules by branch ID
    List<WorkSchedule> findByBranchBranchId(Integer branchId);

    // Find schedules by date range
    List<WorkSchedule> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);

    // Find schedules by user and date range
    List<WorkSchedule> findByUserUserIdAndWorkDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    // Find schedules by branch and date range
    List<WorkSchedule> findByBranchBranchIdAndWorkDateBetween(Integer branchId, LocalDate startDate, LocalDate endDate);

    // Find schedules by status
    List<WorkSchedule> findByStatus(ScheduleStatus status);

    // Find schedules by user and status
    List<WorkSchedule> findByUserUserIdAndStatus(Integer userId, ScheduleStatus status);

    // Find schedules by shift type
    List<WorkSchedule> findByShiftType(ShiftType shiftType);

    // Find schedules by user and date
    List<WorkSchedule> findByUserUserIdAndWorkDate(Integer userId, LocalDate workDate);

    // Find schedules by branch and date
    List<WorkSchedule> findByBranchBranchIdAndWorkDate(Integer branchId, LocalDate workDate);

    // Find schedules by user and month/year
    @Query("SELECT ws FROM WorkSchedule ws WHERE ws.user.userId = :userId AND MONTH(ws.workDate) = :month AND YEAR(ws.workDate) = :year")
    List<WorkSchedule> findByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    // Check for schedule conflicts
    @Query(value = "SELECT * FROM work_schedules ws WHERE ws.user_id = :userId AND ws.work_date = :workDate " +
           "AND ws.status IN ('SCHEDULED', 'RESCHEDULED') " +
           "AND ((CAST(ws.start_time AS TIME) <= CAST(:endTime AS TIME) AND CAST(ws.end_time AS TIME) >= CAST(:startTime AS TIME)))",
           nativeQuery = true)
    List<WorkSchedule> findConflictingSchedules(@Param("userId") Integer userId,
                                               @Param("workDate") LocalDate workDate,
                                               @Param("startTime") LocalTime startTime,
                                               @Param("endTime") LocalTime endTime);

    // Find schedules by user role
    @Query("SELECT ws FROM WorkSchedule ws JOIN ws.user u JOIN u.roles r WHERE r.roleName = :roleName")
    List<WorkSchedule> findByUserRole(@Param("roleName") String roleName);

    // Find schedules by user role and date range
    @Query("SELECT ws FROM WorkSchedule ws JOIN ws.user u JOIN u.roles r " +
           "WHERE r.roleName = :roleName AND ws.workDate BETWEEN :startDate AND :endDate")
    List<WorkSchedule> findByUserRoleAndDateRange(@Param("roleName") String roleName,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // Find schedules by branch and user role
    @Query("SELECT ws FROM WorkSchedule ws JOIN ws.user u JOIN u.roles r " +
           "WHERE r.roleName = :roleName AND ws.branch.branchId = :branchId")
    List<WorkSchedule> findByBranchAndUserRole(@Param("branchId") Integer branchId,
                                              @Param("roleName") String roleName);

    // Find schedules by branch, user role, and date range
    @Query("SELECT ws FROM WorkSchedule ws JOIN ws.user u JOIN u.roles r " +
           "WHERE r.roleName = :roleName AND ws.branch.branchId = :branchId " +
           "AND ws.workDate BETWEEN :startDate AND :endDate")
    List<WorkSchedule> findByBranchAndUserRoleAndDateRange(@Param("branchId") Integer branchId,
                                                          @Param("roleName") String roleName,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);

    // Get weekly schedule for a user
    @Query("SELECT ws FROM WorkSchedule ws WHERE ws.user.userId = :userId " +
           "AND ws.workDate >= :startOfWeek AND ws.workDate <= :endOfWeek " +
           "ORDER BY ws.workDate, ws.startTime")
    List<WorkSchedule> findWeeklySchedule(@Param("userId") Integer userId,
                                         @Param("startOfWeek") LocalDate startOfWeek,
                                         @Param("endOfWeek") LocalDate endOfWeek);

    // Get monthly schedule for a user
    @Query("SELECT ws FROM WorkSchedule ws WHERE ws.user.userId = :userId " +
           "AND YEAR(ws.workDate) = :year AND MONTH(ws.workDate) = :month " +
           "ORDER BY ws.workDate, ws.startTime")
    List<WorkSchedule> findMonthlySchedule(@Param("userId") Integer userId,
                                          @Param("year") int year,
                                          @Param("month") int month);

    // Get schedule coverage for a branch on a specific date
    @Query("SELECT ws FROM WorkSchedule ws JOIN ws.user u JOIN u.roles r " +
           "WHERE ws.branch.branchId = :branchId AND ws.workDate = :date " +
           "AND ws.status IN ('SCHEDULED', 'RESCHEDULED') " +
           "ORDER BY ws.startTime")
    List<WorkSchedule> findBranchScheduleByDate(@Param("branchId") Integer branchId,
                                               @Param("date") LocalDate date);

    // Count schedules by status and date range
    @Query("SELECT COUNT(ws) FROM WorkSchedule ws WHERE ws.status = :status " +
           "AND ws.workDate BETWEEN :startDate AND :endDate")
    Long countByStatusAndDateRange(@Param("status") ScheduleStatus status,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);

    // Find recurring schedules
    List<WorkSchedule> findByIsRecurringTrue();

    // Find recurring schedules ending before a date
    List<WorkSchedule> findByIsRecurringTrueAndRecurringEndDateBefore(LocalDate date);

    // Delete schedules by user ID
    void deleteByUserUserId(Integer userId);

    // Delete schedules by branch ID
    void deleteByBranchBranchId(Integer branchId);
}
