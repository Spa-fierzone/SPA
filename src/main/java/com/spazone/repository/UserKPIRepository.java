package com.spazone.repository;

import com.spazone.entity.UserKPI;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserKPIRepository extends JpaRepository<UserKPI, Integer> {

    // Tìm KPI theo technician, month và year
    Optional<UserKPI> findByTechnicianAndMonthAndYear(User technician, Integer month, Integer year);

    // Tìm tất cả KPI của một technician
    List<UserKPI> findByTechnicianOrderByYearDescMonthDesc(User technician);

    // Tìm KPI theo manager
    List<UserKPI> findByManagerOrderByYearDescMonthDesc(User manager);

    // Tìm KPI theo month/year
    List<UserKPI> findByMonthAndYearOrderByTechnicianFullNameAsc(Integer month, Integer year);

    // Tìm KPI đang active
    List<UserKPI> findByStatusOrderByYearDescMonthDesc(String status);

    // Tìm KPI theo technician và status
    List<UserKPI> findByTechnicianAndStatusOrderByYearDescMonthDesc(User technician, String status);

    // Query để lấy thống kê KPI
    @Query("SELECT k FROM UserKPI k WHERE k.technician.userId = :technicianId AND k.status = 'active' ORDER BY k.year DESC, k.month DESC")
    List<UserKPI> findActivKPIsByTechnician(@Param("technicianId") Integer technicianId);

    // Query để kiểm tra KPI đã tồn tại
    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM UserKPI k WHERE k.technician.userId = :technicianId AND k.month = :month AND k.year = :year")
    boolean existsByTechnicianAndMonthAndYear(@Param("technicianId") Integer technicianId, @Param("month") Integer month, @Param("year") Integer year);

    // Query để lấy thống kê theo manager
    @Query("SELECT k FROM UserKPI k WHERE k.manager.userId = :managerId AND k.month = :month AND k.year = :year ORDER BY k.technician.fullName")
    List<UserKPI> findByManagerAndMonthAndYear(@Param("managerId") Integer managerId, @Param("month") Integer month, @Param("year") Integer year);

    // Methods for Report Service
    @Query("SELECT t.fullName as technicianName, k.targetAppointments as target, k.actualAppointments as actual, k.status as status FROM UserKPI k JOIN k.technician t WHERE k.month = :month AND k.year = :year ORDER BY t.fullName")
    List<Object[]> getKPIReportByMonthRaw(@Param("month") Integer month, @Param("year") Integer year);

    default List<Map<String, Object>> getKPIReportByMonth(Integer month, Integer year) {
        List<Object[]> results = getKPIReportByMonthRaw(month, year);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("technicianName", row[0]);
            map.put("target", row[1]);
            map.put("actual", row[2]);
            map.put("status", row[3]);
            Integer target = (Integer) row[1];
            Integer actual = (Integer) row[2];
            Double completionRate = target > 0 ? (actual * 100.0 / target) : 0.0;
            map.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    // Lấy lịch sử KPI của technician trong khoảng thời gian

    // Query để lấy lịch sử KPI của technician theo thời gian
    @Query("SELECT k FROM UserKPI k WHERE k.technician = :technician AND k.createdAt >= :startDate ORDER BY k.year DESC, k.month DESC")
    List<UserKPI> findByTechnicianAndCreatedAtAfterOrderByYearDescMonthDesc(@Param("technician") User technician, @Param("startDate") java.time.LocalDateTime startDate);

    // Query để tìm KPI theo technician ID, month và year - trả về List để tránh NonUniqueResultException
    @Query("SELECT k FROM UserKPI k WHERE k.technician.userId = :technicianId AND k.month = :month AND k.year = :year ORDER BY k.createdAt DESC")
    List<UserKPI> findKPIsByTechnicianAndMonthAndYear(@Param("technicianId") Integer technicianId, @Param("month") Integer month, @Param("year") Integer year);

}
