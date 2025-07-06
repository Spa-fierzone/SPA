package com.spazone.repository;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.treatmentRecords WHERE a.appointmentId = :id")
    Appointment findByIdWithTreatmentRecords(@Param("id") Integer id);

    @Query("""
    SELECT a FROM Appointment a
    JOIN Invoice i ON i.appointment = a
    WHERE i.paymentStatus = 'unpaid'
""")
    List<Appointment> findCompletedAppointmentsWithUnpaidInvoice();

    @Query("SELECT a FROM Appointment a WHERE a.customer.email = :email ORDER BY a.startTime DESC")
    List<Appointment> findByCustomerEmail(@Param("email") String email);

    @Query("SELECT a FROM Appointment a WHERE a.customer.userId = :customerId AND a.service.serviceId = :serviceId AND a.technician.userId = :technicianId AND a.branch.branchId = :branchId ORDER BY a.createdAt DESC LIMIT 1")
    Appointment findTopByCustomerUserIdAndServiceServiceIdAndTechnicianUserIdAndBranchBranchIdOrderByCreatedAtDesc(
            @Param("customerId") Integer customerId,
            @Param("serviceId") Integer serviceId,
            @Param("technicianId") Integer technicianId,
            @Param("branchId") Integer branchId
    );

    // Method for KPI calculation
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.technician.userId = :technicianId AND a.appointmentDate BETWEEN :startDateTime AND :endDateTime AND a.status IN ('completed', 'confirmed')")
    Integer countByTechnicianUserIdAndAppointmentDateTimeBetween(
            @Param("technicianId") Integer technicianId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // Methods for Report Service
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countByAppointmentDateTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate AND a.status = 'completed'")
    Long countCompletedAppointmentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a.status as status, COUNT(a) as count FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate GROUP BY a.status")
    List<Object[]> getAppointmentCountByStatusRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default Map<String, Long> getAppointmentCountByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getAppointmentCountByStatusRaw(startDate, endDate);
        return results.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> ((Number) row[1]).longValue()  // Convert Number to Long instead of direct casting
        ));
    }

    @Query("SELECT t.fullName as technicianName, COUNT(a) as appointmentCount FROM Appointment a JOIN a.technician t WHERE a.appointmentDate BETWEEN :startDate AND :endDate GROUP BY t.userId, t.fullName ORDER BY COUNT(a) DESC")
    List<Object[]> getAppointmentCountByTechnicianRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Map<String, Object>> getAppointmentCountByTechnician(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getAppointmentCountByTechnicianRaw(startDate, endDate);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("technicianName", row[0]);
            map.put("appointmentCount", ((Number) row[1]).longValue());  // Convert Number to Long
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Query("SELECT s.name as serviceName, COUNT(a) as appointmentCount FROM Appointment a JOIN a.service s WHERE a.appointmentDate BETWEEN :startDate AND :endDate GROUP BY s.serviceId, s.name ORDER BY COUNT(a) DESC")
    List<Object[]> getAppointmentCountByServiceRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Map<String, Object>> getAppointmentCountByService(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getAppointmentCountByServiceRaw(startDate, endDate);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceName", row[0]);
            map.put("appointmentCount", ((Number) row[1]).longValue());  // Convert Number to Long
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Query(value = "SELECT DATEPART(WEEKDAY, appointment_date) as dayOfWeek, COUNT(*) as count FROM appointments WHERE appointment_date BETWEEN :startDate AND :endDate GROUP BY DATEPART(WEEKDAY, appointment_date)", nativeQuery = true)
    List<Object[]> getAppointmentCountByWeekdayRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default Map<String, Long> getAppointmentCountByWeekday(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getAppointmentCountByWeekdayRaw(startDate, endDate);
        String[] dayNames = {"", "Chủ nhật", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7"};
        return results.stream().collect(java.util.stream.Collectors.toMap(
            row -> dayNames[(Integer) row[0]],
            row -> ((Number) row[1]).longValue()  // Convert Number to Long instead of direct casting
        ));
    }

    @Query("SELECT COUNT(DISTINCT a.customer) FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate AND a.customer.userId IN (SELECT a2.customer.userId FROM Appointment a2 WHERE a2.appointmentDate < :startDate)")
    Long countReturningCustomers(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t.fullName as technicianName, COUNT(a) as appointmentCount, AVG(sr.rating) as avgRating FROM Appointment a JOIN a.technician t LEFT JOIN ServiceRating sr ON sr.service = a.service AND sr.customer = a.customer WHERE a.appointmentDate BETWEEN :startDate AND :endDate GROUP BY t.userId, t.fullName ORDER BY COUNT(a) DESC")
    List<Object[]> getTechnicianPerformanceRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Map<String, Object>> getTechnicianPerformance(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getTechnicianPerformanceRaw(startDate, endDate);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("technicianName", row[0]);
            map.put("appointmentCount", row[1]);
            map.put("avgRating", row[2] != null ? row[2] : 0.0);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Query("SELECT s.name as serviceName, COUNT(a) as appointmentCount FROM Appointment a JOIN a.service s WHERE a.appointmentDate BETWEEN :startDate AND :endDate GROUP BY s.serviceId, s.name ORDER BY COUNT(a) DESC")
    List<Object[]> getTopServicesByAppointmentCountRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, org.springframework.data.domain.Pageable pageable);

    default List<Map<String, Object>> getTopServicesByAppointmentCount(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, limit);
        List<Object[]> results = getTopServicesByAppointmentCountRaw(startDate, endDate, pageable);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("serviceName", row[0]);
            map.put("appointmentCount", ((Number) row[1]).longValue());  // Convert Number to Long
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}
