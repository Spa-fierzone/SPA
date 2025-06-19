package com.spazone.repository;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByCustomer(User customer);

    @Query("""
                SELECT a FROM Appointment a
                WHERE a.technician.userId = :technicianId
                  AND a.status IN ('scheduled', 'confirmed')
                  AND (
                      :startDateTime < a.endTime AND :endDateTime > a.startTime
                  )
            """)
    List<Appointment> findConflictingAppointments(
            @Param("technicianId") Integer technicianId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );


    @Query("""
                SELECT a FROM Appointment a
                WHERE a.technician.userId = :technicianId
                  AND a.startTime < :workEnd
                  AND a.endTime > :workStart
                  AND a.status IN ('scheduled', 'confirmed')
                ORDER BY a.startTime ASC
            """)
    List<Appointment> findAppointmentsByTechnicianAndDateRange(
            @Param("technicianId") Integer technicianId,
            @Param("workStart") LocalDateTime workStart,
            @Param("workEnd") LocalDateTime workEnd
    );

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startOfDay AND :endOfDay")
    List<Appointment> findByAppointmentDateRange(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.technician.userId = :techId " +
            "AND a.appointmentDate BETWEEN :start AND :end " +
            "ORDER BY a.appointmentDate ASC")
    Appointment findClosestMatchingAppointment(@Param("techId") Integer techId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    List<Appointment> findByTechnicianUserIdAndAppointmentDateBetween(
            Integer technicianId,
            LocalDateTime start,
            LocalDateTime end
    );
}
