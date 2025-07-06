package com.spazone.service;

import com.spazone.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserKPIRepository userKPIRepository;

    // Báo cáo tổng quan
    public Map<String, Object> getOverviewReport(Integer month, Integer year) {
        Map<String, Object> overview = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Tổng số appointment trong tháng
        Long totalAppointments = appointmentRepository.countByAppointmentDateTimeBetween(startDate, endDate);

        // Tổng doanh thu trong tháng
        BigDecimal totalRevenue = invoiceRepository.getTotalRevenueByDateRange(startDate, endDate);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // Số khách hàng mới trong tháng
        Long newCustomers = userRepository.countNewCustomersByDateRange(startDate, endDate);

        // Số technician hoạt động
        Long activeTechnicians = userRepository.countActiveTechnicians();

        // Tỷ lệ hoàn thành appointment
        Long completedAppointments = appointmentRepository.countCompletedAppointmentsByDateRange(startDate, endDate);
        Double completionRate = totalAppointments > 0 ? (completedAppointments * 100.0 / totalAppointments) : 0.0;

        // So sánh với tháng trước
        LocalDateTime prevStartDate = YearMonth.of(year, month).minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime prevEndDate = YearMonth.of(year, month).minusMonths(1).atEndOfMonth().atTime(23, 59, 59);

        Long prevAppointments = appointmentRepository.countByAppointmentDateTimeBetween(prevStartDate, prevEndDate);
        BigDecimal prevRevenue = invoiceRepository.getTotalRevenueByDateRange(prevStartDate, prevEndDate);
        if (prevRevenue == null) prevRevenue = BigDecimal.ZERO;

        // Tính tỷ lệ tăng trưởng
        Double appointmentGrowth = prevAppointments > 0 ? ((totalAppointments - prevAppointments) * 100.0 / prevAppointments) : 0.0;
        Double revenueGrowth = prevRevenue.compareTo(BigDecimal.ZERO) > 0 ?
            totalRevenue.subtract(prevRevenue).multiply(BigDecimal.valueOf(100)).divide(prevRevenue, 2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0.0;

        overview.put("totalAppointments", totalAppointments);
        overview.put("totalRevenue", totalRevenue);
        overview.put("newCustomers", newCustomers);
        overview.put("activeTechnicians", activeTechnicians);
        overview.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        overview.put("appointmentGrowth", Math.round(appointmentGrowth * 100.0) / 100.0);
        overview.put("revenueGrowth", Math.round(revenueGrowth * 100.0) / 100.0);

        return overview;
    }

    // Báo cáo doanh thu
    public Map<String, Object> getRevenueReport(Integer month, Integer year) {
        Map<String, Object> revenue = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Tổng doanh thu
        BigDecimal totalRevenue = invoiceRepository.getTotalRevenueByDateRange(startDate, endDate);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // Doanh thu theo ngày trong tháng
        List<Map<String, Object>> dailyRevenue = invoiceRepository.getDailyRevenueByDateRange(startDate, endDate);

        // Doanh thu theo dịch vụ
        List<Map<String, Object>> revenueByService = invoiceRepository.getRevenueByService(startDate, endDate);

        // Doanh thu theo thanh toán
        Map<String, BigDecimal> paymentStatus = invoiceRepository.getRevenueByPaymentStatus(startDate, endDate);

        revenue.put("totalRevenue", totalRevenue);
        revenue.put("dailyRevenue", dailyRevenue);
        revenue.put("revenueByService", revenueByService);
        revenue.put("paymentStatus", paymentStatus);

        return revenue;
    }

    // Báo cáo appointment
    public Map<String, Object> getAppointmentReport(Integer month, Integer year) {
        Map<String, Object> appointment = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Thống kê theo trạng thái
        Map<String, Long> statusCount = appointmentRepository.getAppointmentCountByStatus(startDate, endDate);

        // Thống kê theo technician
        List<Map<String, Object>> appointmentByTechnician = appointmentRepository.getAppointmentCountByTechnician(startDate, endDate);

        // Thống kê theo dịch vụ
        List<Map<String, Object>> appointmentByService = appointmentRepository.getAppointmentCountByService(startDate, endDate);

        // Thống kê theo ngày trong tuần
        Map<String, Long> appointmentByWeekday = appointmentRepository.getAppointmentCountByWeekday(startDate, endDate);

        appointment.put("statusCount", statusCount);
        appointment.put("appointmentByTechnician", appointmentByTechnician);
        appointment.put("appointmentByService", appointmentByService);
        appointment.put("appointmentByWeekday", appointmentByWeekday);

        return appointment;
    }

    // Báo cáo khách hàng
    public Map<String, Object> getCustomerReport(Integer month, Integer year) {
        Map<String, Object> customer = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Khách hàng mới
        Long newCustomers = userRepository.countNewCustomersByDateRange(startDate, endDate);

        // Tổng số khách hàng
        Long totalCustomers = userRepository.countTotalCustomers();

        // Khách hàng quay lại
        Long returningCustomers = appointmentRepository.countReturningCustomers(startDate, endDate);

        // Top khách hàng chi tiêu nhiều nhất
        List<Map<String, Object>> topSpendingCustomers = invoiceRepository.getTopSpendingCustomers(startDate, endDate, 10);

        customer.put("newCustomers", newCustomers);
        customer.put("totalCustomers", totalCustomers);
        customer.put("returningCustomers", returningCustomers);
        customer.put("topSpendingCustomers", topSpendingCustomers);

        return customer;
    }

    // Báo cáo technician
    public Map<String, Object> getTechnicianReport(Integer month, Integer year) {
        Map<String, Object> technician = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Hiệu suất technician
        List<Map<String, Object>> technicianPerformance = appointmentRepository.getTechnicianPerformance(startDate, endDate);

        // KPI technician
        List<Map<String, Object>> technicianKPI = userKPIRepository.getKPIReportByMonth(month, year);

        technician.put("performance", technicianPerformance);
        technician.put("kpi", technicianKPI);

        return technician;
    }

    // Báo cáo dịch vụ
    public Map<String, Object> getServiceReport(Integer month, Integer year) {
        Map<String, Object> service = new HashMap<>();

        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        // Top dịch vụ được đặt nhiều nhất
        List<Map<String, Object>> topServices = appointmentRepository.getTopServicesByAppointmentCount(startDate, endDate, 10);

        // Doanh thu theo dịch vụ
        List<Map<String, Object>> serviceRevenue = invoiceRepository.getRevenueByService(startDate, endDate);

        service.put("topServices", topServices);
        service.put("serviceRevenue", serviceRevenue);

        return service;
    }

    // Dữ liệu cho biểu đồ doanh thu
    public Map<String, Object> getRevenueChartData(Integer month, Integer year) {
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> dailyRevenue = invoiceRepository.getDailyRevenueByDateRange(startDate, endDate);

        List<String> labels = new ArrayList<>();
        List<BigDecimal> data = new ArrayList<>();

        for (Map<String, Object> item : dailyRevenue) {
            labels.add(item.get("day").toString());
            data.add((BigDecimal) item.get("revenue"));
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);

        return chartData;
    }

    // Dữ liệu cho biểu đồ appointment
    public Map<String, Object> getAppointmentChartData(Integer month, Integer year) {
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        Map<String, Long> statusCount = appointmentRepository.getAppointmentCountByStatus(startDate, endDate);

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", new ArrayList<>(statusCount.keySet()));
        chartData.put("data", new ArrayList<>(statusCount.values()));

        return chartData;
    }

    // Dữ liệu cho biểu đồ dịch vụ
    public Map<String, Object> getServiceChartData(Integer month, Integer year) {
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> topServices = appointmentRepository.getTopServicesByAppointmentCount(startDate, endDate, 10);

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Map<String, Object> item : topServices) {
            labels.add((String) item.get("serviceName"));
            data.add((Long) item.get("appointmentCount"));
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);

        return chartData;
    }

    // Dữ liệu cho biểu đồ hiệu suất technician
    public Map<String, Object> getTechnicianPerformanceChartData(Integer month, Integer year) {
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

        List<Map<String, Object>> performance = appointmentRepository.getTechnicianPerformance(startDate, endDate);

        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (Map<String, Object> item : performance) {
            labels.add((String) item.get("technicianName"));
            data.add((Long) item.get("appointmentCount"));
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);

        return chartData;
    }

    // Các method báo cáo chi tiết khác
    public Map<String, Object> getDetailedRevenueReport(Integer month, Integer year) {
        // Implementation for detailed revenue report
        return getRevenueReport(month, year);
    }

    public Map<String, Object> getTechnicianPerformanceReport(Integer month, Integer year) {
        // Implementation for detailed technician performance report
        return getTechnicianReport(month, year);
    }
}
