package com.spazone.controller;

import com.spazone.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class AdminReportController {

    @Autowired
    private ReportService reportService;

    // Trang báo cáo chính
    @GetMapping
    public String reportsPage(@RequestParam(value = "month", required = false) Integer month,
                             @RequestParam(value = "year", required = false) Integer year,
                             Model model) {

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        // Lấy dữ liệu báo cáo tổng quan
        Map<String, Object> overviewData = reportService.getOverviewReport(month, year);

        // Lấy dữ liệu báo cáo doanh thu
        Map<String, Object> revenueData = reportService.getRevenueReport(month, year);

        // Lấy dữ liệu báo cáo appointment
        Map<String, Object> appointmentData = reportService.getAppointmentReport(month, year);

        // Lấy dữ liệu báo cáo khách hàng
        Map<String, Object> customerData = reportService.getCustomerReport(month, year);

        // Lấy dữ liệu báo cáo technician
        Map<String, Object> technicianData = reportService.getTechnicianReport(month, year);

        // Lấy dữ liệu báo cáo dịch vụ
        Map<String, Object> serviceData = reportService.getServiceReport(month, year);

        model.addAttribute("overviewData", overviewData);
        model.addAttribute("revenueData", revenueData);
        model.addAttribute("appointmentData", appointmentData);
        model.addAttribute("customerData", customerData);
        model.addAttribute("technicianData", technicianData);
        model.addAttribute("serviceData", serviceData);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());

        return "admin/reports";
    }

    // API endpoint cho dữ liệu chart
    @GetMapping("/chart-data")
    @ResponseBody
    public Map<String, Object> getChartData(@RequestParam(value = "type") String type,
                                           @RequestParam(value = "month", required = false) Integer month,
                                           @RequestParam(value = "year", required = false) Integer year) {

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        return switch (type) {
            case "revenue" -> reportService.getRevenueChartData(month, year);
            case "appointment" -> reportService.getAppointmentChartData(month, year);
            case "service" -> reportService.getServiceChartData(month, year);
            case "technician" -> reportService.getTechnicianPerformanceChartData(month, year);
            default -> Map.of("error", "Invalid chart type");
        };
    }

    // Báo cáo doanh thu chi tiết
    @GetMapping("/revenue")
    public String revenueReport(@RequestParam(value = "month", required = false) Integer month,
                               @RequestParam(value = "year", required = false) Integer year,
                               Model model) {

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        Map<String, Object> revenueData = reportService.getDetailedRevenueReport(month, year);

        model.addAttribute("revenueData", revenueData);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);

        return "admin/revenue-report";
    }
}
