package com.spazone.controller.technician;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import com.spazone.service.AppointmentService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        // Get today's appointments
        List<Appointment> todayAppointments = appointmentService.getTodayAppointmentsByTechnician(technician);

        // Get upcoming appointments (next 7 days)
        List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointmentsByTechnician(technician, 7);

        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("technician", technician);

        return "technician/dashboard";
    }

    @GetMapping("/appointments")
    public String viewAppointments(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        LocalDate filterDate = null;
        if (date != null && !date.isEmpty()) {
            filterDate = LocalDate.parse(date);
        }

        Page<Appointment> appointmentPage = appointmentService.getAppointmentsByTechnicianWithFilters(
                technician, status, filterDate, pageable);

        model.addAttribute("appointments", appointmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentPage.getTotalPages());
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        model.addAttribute("technician", technician);

        return "technician/appointments/list";
    }

    @GetMapping("/appointments/{id}")
    public String viewAppointmentDetail(
            @PathVariable Integer id,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null || !appointment.getTechnician().getUserId().equals(technician.getUserId())) {
            return "redirect:/technician/appointments";
        }

        model.addAttribute("appointment", appointment);
        return "technician/appointments/detail";
    }

    @PostMapping("/appointments/{id}/checkin")
    public String checkIn(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null && appointment.getTechnician().getUserId().equals(technician.getUserId())) {
                appointment.setStatus("checked_in");
                appointment.setCheckedInAt(LocalDateTime.now());
                appointmentService.save(appointment);
                redirectAttributes.addFlashAttribute("message", "Đã check-in thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi check-in: " + e.getMessage());
        }

        return "redirect:/technician/appointments/" + id;
    }

    @PostMapping("/appointments/{id}/start-treatment")
    public String startTreatment(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null && appointment.getTechnician().getUserId().equals(technician.getUserId())) {
                appointment.setStatus("in_progress");
                appointment.setTreatmentStartedAt(LocalDateTime.now());
                appointmentService.save(appointment);
                redirectAttributes.addFlashAttribute("message", "Đã bắt đầu điều trị!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi bắt đầu điều trị: " + e.getMessage());
        }

        return "redirect:/technician/appointments/" + id;
    }

    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(
            @PathVariable Integer id,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null && appointment.getTechnician().getUserId().equals(technician.getUserId())) {
                appointment.setStatus("completed");
                appointment.setCompletedAt(LocalDateTime.now());
                if (notes != null && !notes.trim().isEmpty()) {
                    appointment.setNotes(notes);
                }
                appointmentService.save(appointment);
                redirectAttributes.addFlashAttribute("message", "Đã hoàn thành lịch hẹn!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi hoàn thành lịch hẹn: " + e.getMessage());
        }

        return "redirect:/technician/appointments/" + id;
    }

    @GetMapping("/appointments/{id}/add-notes")
    public String showAddNotesForm(
            @PathVariable Integer id,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        Appointment appointment = appointmentService.getById(id);
        if (appointment == null || !appointment.getTechnician().getUserId().equals(technician.getUserId())) {
            return "redirect:/technician/appointments";
        }

        model.addAttribute("appointment", appointment);
        return "technician/appointments/add-notes";
    }

    @PostMapping("/appointments/{id}/add-notes")
    public String addNotes(
            @PathVariable Integer id,
            @RequestParam("notes") String notes,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        try {
            Appointment appointment = appointmentService.getById(id);
            if (appointment != null && appointment.getTechnician().getUserId().equals(technician.getUserId())) {
                appointment.setNotes(notes);
                appointmentService.save(appointment);
                redirectAttributes.addFlashAttribute("message", "Đã thêm ghi chú thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm ghi chú: " + e.getMessage());
        }

        return "redirect:/technician/appointments/" + id;
    }

    @GetMapping("/schedule")
    public String viewSchedule(
            @RequestParam(value = "date", required = false) String date,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        LocalDate scheduleDate = LocalDate.now();
        if (date != null && !date.isEmpty()) {
            scheduleDate = LocalDate.parse(date);
        }

        List<Appointment> dayAppointments = appointmentService.getAppointmentsByTechnicianAndDate(
                technician, scheduleDate);

        model.addAttribute("appointments", dayAppointments);
        model.addAttribute("scheduleDate", scheduleDate);
        model.addAttribute("technician", technician);

        return "technician/schedule";
    }

    @GetMapping("/work-history")
    public String viewWorkHistory(
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

        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        LocalDate start = null;
        LocalDate end = null;

        if (startDate != null && !startDate.isEmpty()) {
            start = LocalDate.parse(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = LocalDate.parse(endDate);
        }

        Page<Appointment> historyPage = appointmentService.getCompletedAppointmentsByTechnicianWithDateRange(
                technician, start, end, pageable);

        model.addAttribute("appointments", historyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", historyPage.getTotalPages());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("technician", technician);

        return "technician/work-history";
    }

    @GetMapping("/customer-info/{customerId}")
    public String viewCustomerInfo(
            @PathVariable Integer customerId,
            Authentication authentication,
            Model model) {

        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }

        User customer = userService.getUserById(customerId);
        if (customer == null) {
            return "redirect:/technician/appointments";
        }

        // Get customer's appointment history with this technician
        String username = authentication.getName();
        User technician = userService.findByEmail(username);

        List<Appointment> customerHistory = appointmentService.getCustomerAppointmentHistoryWithTechnician(
                customer, technician);

        model.addAttribute("customer", customer);
        model.addAttribute("appointmentHistory", customerHistory);

        return "technician/customer-info";
    }
}