package com.spazone.service;

import com.spazone.entity.UserKPI;
import com.spazone.entity.User;
import com.spazone.entity.Appointment;
import com.spazone.repository.UserKPIRepository;
import com.spazone.repository.UserRepository;
import com.spazone.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserKPIService {

    @Autowired
    private UserKPIRepository userKPIRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Tạo hoặc cập nhật KPI cho technician
    public UserKPI createOrUpdateKPI(Integer technicianId, Integer managerId, Integer month, Integer year, Integer targetAppointments) {
        Optional<User> technician = userRepository.findById(technicianId);
        Optional<User> manager = userRepository.findById(managerId);

        if (technician.isEmpty() || manager.isEmpty()) {
            throw new RuntimeException("Technician hoặc Manager không tồn tại");
        }

        // Kiểm tra xem KPI đã tồn tại chưa - sử dụng List để tránh NonUniqueResultException
        List<UserKPI> existingKPIs = userKPIRepository.findKPIsByTechnicianAndMonthAndYear(technicianId, month, year);

        UserKPI kpi;
        if (!existingKPIs.isEmpty()) {
            // Cập nhật KPI hiện có (lấy KPI active đầu tiên hoặc KPI đầu tiên)
            kpi = existingKPIs.stream()
                    .filter(k -> "active".equals(k.getStatus()))
                    .findFirst()
                    .orElse(existingKPIs.get(0));
            kpi.setTargetAppointments(targetAppointments);
            kpi.setManager(manager.get());
            kpi.setUpdatedAt(LocalDateTime.now());
        } else {
            // Tạo KPI mới
            kpi = new UserKPI(technician.get(), manager.get(), month, year, targetAppointments);
        }

        return userKPIRepository.save(kpi);
    }

    // Lấy danh sách KPI theo manager
    public List<UserKPI> getKPIsByManager(Integer managerId) {
        Optional<User> manager = userRepository.findById(managerId);
        if (manager.isEmpty()) {
            throw new RuntimeException("Manager không tồn tại");
        }
        return userKPIRepository.findByManagerOrderByYearDescMonthDesc(manager.get());
    }

    // Lấy danh sách KPI theo technician
    public List<UserKPI> getKPIsByTechnician(Integer technicianId) {
        Optional<User> technician = userRepository.findById(technicianId);
        if (technician.isEmpty()) {
            throw new RuntimeException("Technician không tồn tại");
        }
        return userKPIRepository.findByTechnicianOrderByYearDescMonthDesc(technician.get());
    }

    // Lấy KPI theo month/year
    public List<UserKPI> getKPIsByMonthAndYear(Integer month, Integer year) {
        return userKPIRepository.findByMonthAndYearOrderByTechnicianFullNameAsc(month, year);
    }

    // Lấy KPI theo manager và month/year
    public List<UserKPI> getKPIsByManagerAndMonthAndYear(Integer managerId, Integer month, Integer year) {
        return userKPIRepository.findByManagerAndMonthAndYear(managerId, month, year);
    }

    // Cập nhật actual appointments cho tất cả KPI trong tháng
    public void updateActualAppointments(Integer month, Integer year) {
        List<UserKPI> kpis = userKPIRepository.findByMonthAndYearOrderByTechnicianFullNameAsc(month, year);

        for (UserKPI kpi : kpis) {
            Integer actualCount = getActualAppointmentCount(kpi.getTechnician().getUserId(), month, year);
            kpi.setActualAppointments(actualCount);
            kpi.setUpdatedAt(LocalDateTime.now());
            userKPIRepository.save(kpi);
        }
    }

    // Đếm số lịch hẹn thực tế của technician trong tháng
    private Integer getActualAppointmentCount(Integer technicianId, Integer month, Integer year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Đếm appointments của technician trong tháng
        return appointmentRepository.countByTechnicianUserIdAndAppointmentDateTimeBetween(
            technicianId, startDateTime, endDateTime);
    }

    // Lấy tất cả technicians (users có role technician)
    public List<User> getAllTechnicians() {
        return userRepository.findByRoles_RoleName("TECHNICIAN");
    }

    // Xóa KPI
    public void deleteKPI(Integer kpiId) {
        userKPIRepository.deleteById(kpiId);
    }

    // Lấy KPI theo ID
    public Optional<UserKPI> getKPIById(Integer kpiId) {
        return userKPIRepository.findById(kpiId);
    }

    // Kiểm tra KPI đã tồn tại
    public boolean isKPIExists(Integer technicianId, Integer month, Integer year) {
        return userKPIRepository.existsByTechnicianAndMonthAndYear(technicianId, month, year);
    }

    // Lấy KPI hiện tại của technician (tháng hiện tại)
    public Optional<UserKPI> getCurrentKPI(Integer technicianId) {
        LocalDate now = LocalDate.now();
        Optional<User> technician = userRepository.findById(technicianId);
        if (technician.isEmpty()) {
            return Optional.empty();
        }

        // Sử dụng List query để tránh NonUniqueResultException
        List<UserKPI> kpiList = userKPIRepository.findKPIsByTechnicianAndMonthAndYear(
            technicianId, now.getMonthValue(), now.getYear());

        if (kpiList.isEmpty()) {
            return Optional.empty();
        }

        // Trả về KPI active đầu tiên hoặc KPI đầu tiên
        UserKPI kpi = kpiList.stream()
            .filter(k -> "active".equals(k.getStatus()))
            .findFirst()
            .orElse(kpiList.get(0));

        return Optional.of(kpi);
    }

    // Cập nhật trạng thái KPI
    public UserKPI updateKPIStatus(Integer kpiId, String status) {
        Optional<UserKPI> kpi = userKPIRepository.findById(kpiId);
        if (kpi.isEmpty()) {
            throw new RuntimeException("KPI không tồn tại");
        }

        UserKPI userKPI = kpi.get();
        userKPI.setStatus(status);
        userKPI.setUpdatedAt(LocalDateTime.now());
        return userKPIRepository.save(userKPI);
    }

    // Lấy KPI của technician theo tháng và năm
    public List<UserKPI> getKPIsByTechnicianAndMonthAndYear(Integer technicianId, Integer month, Integer year) {
        Optional<User> technician = userRepository.findById(technicianId);
        if (technician.isEmpty()) {
            throw new RuntimeException("Technician không tồn tại");
        }

        // Sử dụng List query để tránh NonUniqueResultException
        return userKPIRepository.findKPIsByTechnicianAndMonthAndYear(technicianId, month, year);
    }

    // Lấy lịch sử KPI của technician (n tháng gần nhất)
    public List<UserKPI> getKPIHistoryByTechnician(Integer technicianId, int months) {
        Optional<User> technician = userRepository.findById(technicianId);
        if (technician.isEmpty()) {
            throw new RuntimeException("Technician không tồn tại");
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(months);

        return userKPIRepository.findByTechnicianAndCreatedAtAfterOrderByYearDescMonthDesc(
            technician.get(), startDate.atStartOfDay());
    }

    // Cập nhật KPI khi có appointment mới được tạo
    public void updateKPIOnAppointmentCreated(Integer technicianId, Integer serviceDuration) {
        try {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            // Tìm KPI hiện tại của technician
            Optional<User> technician = userRepository.findById(technicianId);
            if (technician.isEmpty()) {
                return; // Nếu không tìm thấy technician thì không cập nhật KPI
            }

            // Tìm KPI theo technician, month và year - sử dụng query trực tiếp để tránh NonUniqueResultException
            List<UserKPI> kpiList = userKPIRepository.findKPIsByTechnicianAndMonthAndYear(
                technicianId, currentMonth, currentYear);

            if (!kpiList.isEmpty()) {
                // Lấy KPI đầu tiên (hoặc active đầu tiên nếu có)
                UserKPI kpi = kpiList.stream()
                    .filter(k -> "active".equals(k.getStatus()))
                    .findFirst()
                    .orElse(kpiList.get(0));

                // Cập nhật số lượng appointments thực tế
                int currentActual = kpi.getActualAppointments() != null ? kpi.getActualAppointments() : 0;
                kpi.setActualAppointments(currentActual + 1);
                kpi.setUpdatedAt(LocalDateTime.now());
                userKPIRepository.save(kpi);
            }
            // Nếu chưa có KPI cho tháng này, không tạo mới mà chỉ bỏ qua
            // KPI phải được tạo trước bởi manager
        } catch (Exception e) {
            // Log error nhưng không throw exception để không ảnh hưởng đến việc tạo appointment
            System.err.println("Error updating KPI for technician " + technicianId + ": " + e.getMessage());
        }
    }
}
