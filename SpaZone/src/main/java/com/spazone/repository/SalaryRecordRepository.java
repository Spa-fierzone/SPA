package com.spazone.repository;

import com.spazone.entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Integer> {

    @Query("SELECT sr FROM SalaryRecord sr WHERE sr.user.userId = :userId AND sr.month = :month AND sr.year = :year")
    Optional<SalaryRecord> findByUserAndMonthYear(@Param("userId") Integer userId, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT sr FROM SalaryRecord sr WHERE sr.month = :month AND sr.year = :year")
    List<SalaryRecord> findByMonthYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT sr FROM SalaryRecord sr WHERE sr.month = :month AND sr.year = :year AND sr.branch.branchId = :branchId")
    List<SalaryRecord> findByMonthYearAndBranch(@Param("month") Integer month, @Param("year") Integer year, @Param("branchId") Integer branchId);
}