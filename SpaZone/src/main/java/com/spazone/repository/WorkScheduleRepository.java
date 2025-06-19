package com.spazone.repository;

import com.spazone.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Integer> {

    Optional<WorkSchedule> findByUserUserIdAndWorkDate(Integer userId, LocalDate workDate);

    List<WorkSchedule> findByUserUserId(Integer userId);

    List<WorkSchedule> findByBranchBranchIdAndWorkDate(Integer branchId, LocalDate workDate);

    List<WorkSchedule> findByWorkDate(LocalDate workDate);

    List<WorkSchedule> findByUserUserIdAndWorkDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT ws FROM WorkSchedule ws WHERE ws.user.userId = :userId AND MONTH(ws.workDate) = :month AND YEAR(ws.workDate) = :year")
    List<WorkSchedule> findByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT COUNT(ws) FROM WorkSchedule ws WHERE ws.user.userId = :userId AND MONTH(ws.workDate) = :month AND YEAR(ws.workDate) = :year")
    Long countScheduledDaysByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);
}
