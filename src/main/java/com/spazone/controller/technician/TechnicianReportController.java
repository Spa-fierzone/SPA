package com.spazone.controller.technician;

import com.spazone.dto.TechnicianKpiDto;
import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import com.spazone.service.AppointmentService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/technician")
public class TechnicianReportController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/kpi")
    public String viewKpi(
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "year", required = false) String year,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        // Default to current month/year if not provided
        YearMonth targetMonth = YearMonth.now();
        if (month != null && year != null) {
            try {
                targetMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
            } catch (NumberFormatException e) {
                // Use current month if parsing fails
            }
        }

        // Get KPI data
        TechnicianKpiDto kpiData = appointmentService.getTechnicianKpiData(technician, targetMonth);

        // Get daily performance for the month
        Map<LocalDate, Integer> dailyPerformance = appointmentService.getDailyAppointmentCount(
                technician, targetMonth);

        model.addAttribute("kpiData", kpiData);
        model.addAttribute("dailyPerformance", dailyPerformance);
        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("technician", technician);

        return "technician/kpi";
    }

    @GetMapping("/notifications")
    public String viewNotifications(
            @RequestParam(value = "type", required = false) String type,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        // Get upcoming appointments for today and tomorrow
        List<Appointment> todayAppointments = appointmentService.getTodayAppointmentsByTechnician(technician);
        List<Appointment> tomorrowAppointments = appointmentService.getTomorrowAppointmentsByTechnician(technician);

        // Get overdue appointments (should have been completed but are still pending)
        List<Appointment> overdueAppointments = appointmentService.getOverdueAppointmentsByTechnician(technician);

        // Get appointments requiring follow-up
        List<Appointment> followUpAppointments = appointmentService.getAppointmentsRequiringFollowUp(technician);

        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("tomorrowAppointments", tomorrowAppointments);
        model.addAttribute("overdueAppointments", overdueAppointments);
        model.addAttribute("followUpAppointments", followUpAppointments);
        model.addAttribute("filterType", type);

        return "technician/notifications";
    }

    @GetMapping("/performance-report")
    public String viewPerformanceReport(
            @RequestParam(value = "period", defaultValue = "month") String period,
            @RequestParam(value = "date", required = false) String date,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        LocalDate reportDate = LocalDate.now();
        if (date != null && !date.isEmpty()) {
            reportDate = LocalDate.parse(date);
        }

        Map<String, Object> performanceData;

        switch (period) {
            case "week":
                performanceData = appointmentService.getWeeklyPerformanceReport(technician, reportDate);
                break;
            case "quarter":
                performanceData = appointmentService.getQuarterlyPerformanceReport(technician, reportDate);
                break;
            default: // month
                performanceData = appointmentService.getMonthlyPerformanceReport(technician, reportDate);
                break;
        }

        model.addAttribute("performanceData", performanceData);
        model.addAttribute("period", period);
        model.addAttribute("reportDate", reportDate);
        model.addAttribute("technician", technician);

        return "technician/performance-report";
    }

    @GetMapping("/customer-feedback")
    public String viewCustomerFeedback(
            @RequestParam(value = "page", defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        // Get appointments with customer feedback/ratings
        List<Appointment> appointmentsWithFeedback = appointmentService.getAppointmentsWithFeedback(
                technician, page, 10);

        // Calculate average rating
        Double averageRating = appointmentService.getAverageRatingForTechnician(technician);

        // Get recent feedback summary
        Map<String, Object> feedbackSummary = appointmentService.getFeedbackSummary(technician);

        model.addAttribute("appointmentsWithFeedback", appointmentsWithFeedback);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("feedbackSummary", feedbackSummary);
        model.addAttribute("currentPage", page);

        return "technician/customer-feedback";
    }

    @GetMapping("/treatment-history")
    public String viewTreatmentHistory(
            @RequestParam(value = "serviceId", required = false) Integer serviceId,
            @RequestParam(value = "customerId", required = false) Integer customerId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        LocalDate start = null;
        LocalDate end = null;

        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate);
        }

        List<Appointment> treatmentHistory = appointmentService.getTreatmentHistoryWithFilters(
                technician, serviceId, customerId, start, end, page, 15);

        // Get filter options
        List<com.spazone.entity.Service> services = appointmentService.getServicesUsedByTechnician(technician);
        List<User> customers = appointmentService.getCustomersServedByTechnician(technician);

        model.addAttribute("treatmentHistory", treatmentHistory);
        model.addAttribute("services", services);
        model.addAttribute("customers", customers);
        model.addAttribute("serviceId", serviceId);
        model.addAttribute("customerId", customerId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);

        return "technician/treatment-history";
    }
}