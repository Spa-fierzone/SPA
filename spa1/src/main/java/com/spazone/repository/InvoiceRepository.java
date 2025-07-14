package com.spazone.repository;

import com.spazone.entity.Appointment;
import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Invoice findByAppointment(Appointment appointment);

    List<Invoice> findByCustomer(User customer);

    // Methods for Report Service
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate AND i.paymentStatus = 'paid'")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT CONVERT(date, created_at) as day, COALESCE(SUM(total_amount), 0) as revenue FROM invoices WHERE created_at BETWEEN :startDate AND :endDate AND payment_status = 'paid' GROUP BY CONVERT(date, created_at) ORDER BY CONVERT(date, created_at)", nativeQuery = true)
    List<Object[]> getDailyRevenueByDateRangeRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Map<String, Object>> getDailyRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getDailyRevenueByDateRangeRaw(startDate, endDate);
        return results.stream().map(row -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("day", row[0]);
            map.put("revenue", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Query("SELECT s.name as serviceName, COALESCE(SUM(i.totalAmount), 0) as revenue FROM Invoice i JOIN i.appointment a JOIN a.service s WHERE i.createdAt BETWEEN :startDate AND :endDate AND i.paymentStatus = 'paid' GROUP BY s.serviceId, s.name ORDER BY SUM(i.totalAmount) DESC")
    List<Object[]> getRevenueByServiceRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default List<Map<String, Object>> getRevenueByService(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getRevenueByServiceRaw(startDate, endDate);
        return results.stream().map(row -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("serviceName", row[0]);
            map.put("revenue", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Query("SELECT i.paymentStatus as status, COALESCE(SUM(i.totalAmount), 0) as revenue FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate GROUP BY i.paymentStatus")
    List<Object[]> getRevenueByPaymentStatusRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    default Map<String, BigDecimal> getRevenueByPaymentStatus(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = getRevenueByPaymentStatusRaw(startDate, endDate);
        return results.stream().collect(java.util.stream.Collectors.toMap(
            row -> (String) row[0],
            row -> (BigDecimal) row[1]
        ));
    }

    @Query("SELECT c.fullName as customerName, COALESCE(SUM(i.totalAmount), 0) as totalSpent FROM Invoice i JOIN i.customer c WHERE i.createdAt BETWEEN :startDate AND :endDate AND i.paymentStatus = 'paid' GROUP BY c.userId, c.fullName ORDER BY SUM(i.totalAmount) DESC LIMIT :limit")
    List<Object[]> getTopSpendingCustomersRaw(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("limit") int limit);

    default List<Map<String, Object>> getTopSpendingCustomers(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        List<Object[]> results = getTopSpendingCustomersRaw(startDate, endDate, limit);
        return results.stream().map(row -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("customerName", row[0]);
            map.put("totalSpent", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}
