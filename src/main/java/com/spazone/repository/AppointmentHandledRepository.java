package com.spazone.repository;

import com.spazone.entity.AppointmentHandled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentHandledRepository extends JpaRepository<AppointmentHandled, Long> {

    @Query("SELECT COUNT(a) FROM AppointmentHandled a WHERE a.receptionist.userId = :userId AND MONTH(a.handledAt) = :month AND YEAR(a.handledAt) = :year")
    int countByReceptionistAndMonth(@Param("userId") Integer userId, @Param("month") int month, @Param("year") int year);

    @Query("""
    SELECT a FROM AppointmentHandled a
    WHERE a.receptionist.userId = :receptionistId
      AND MONTH(a.handledAt) = :month
      AND YEAR(a.handledAt) = :year
""")
    List<AppointmentHandled> findByReceptionistAndMonth(
            @Param("receptionistId") Integer receptionistId,
            @Param("month") int month,
            @Param("year") int year
    );


}
