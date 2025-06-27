package com.spazone.controller;

import com.spazone.entity.Attendance;
import com.spazone.entity.User;
import com.spazone.service.AttendanceService;
import com.spazone.service.BranchService;
import com.spazone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @GetMapping
    public String attendancePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Optional<Attendance> todayAttendance = attendanceService.getTodayAttendance(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("todayAttendance", todayAttendance.orElse(null));
        model.addAttribute("branches", branchService.findAllActive());
        model.addAttribute("currentTime", LocalDateTime.now());

        return "attendance/index";
    }

    @PostMapping("/checkin")
    public String checkIn(@RequestParam Integer branchId,
                          @RequestParam(required = false) String notes,
                          @AuthenticationPrincipal UserDetails userDetails,
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            String ipAddress = getClientIpAddress(request);
            String location = "Office"; // You can implement geolocation later

            Attendance attendance = attendanceService.checkIn(user.getUserId(), branchId, location, ipAddress);
            if (notes != null && !notes.trim().isEmpty()) {
                attendance.setNotes(notes);
                attendanceService.save(attendance);
            }

            redirectAttributes.addFlashAttribute("success", "Check-in successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/attendance";
    }

    @PostMapping("/checkout")
    public String checkOut(@RequestParam(required = false) String notes,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            Attendance attendance = attendanceService.checkOut(user.getUserId());

            if (notes != null && !notes.trim().isEmpty()) {
                attendance.setNotes(attendance.getNotes() + " | Checkout: " + notes);
                attendanceService.save(attendance);
            }

            redirectAttributes.addFlashAttribute("success", "Check-out successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/attendance";
    }

    @PostMapping("/break/start")
    public String startBreak(@AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            attendanceService.startBreak(user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Break started!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/attendance";
    }

    @PostMapping("/break/end")
    public String endBreak(@AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            attendanceService.endBreak(user.getUserId());
            redirectAttributes.addFlashAttribute("success", "Break ended!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/attendance";
    }

    @GetMapping("/history")
    public String attendanceHistory(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size);
        Page<Attendance> attendancePage = attendanceService.getUserAttendanceHistory(user.getUserId(), pageable);

        model.addAttribute("attendancePage", attendancePage);
        model.addAttribute("user", user);

        return "attendance/history";
    }

    @GetMapping("/report")
    public String attendanceReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (startDate == null) startDate = LocalDate.now().withDayOfMonth(1);
        if (endDate == null) endDate = LocalDate.now();

        List<Attendance> attendances = attendanceService.getUserAttendanceByDateRange(user.getUserId(), startDate, endDate);
        Long presentDays = attendanceService.getPresentDaysCount(user.getUserId(), startDate, endDate);
        Long lateDays = attendanceService.getLateDaysCount(user.getUserId(), startDate, endDate);

        model.addAttribute("attendances", attendances);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("presentDays", presentDays);
        model.addAttribute("lateDays", lateDays);
        model.addAttribute("user", user);

        return "attendance/report";
    }

    @GetMapping("/admin/today")
    public String todayAttendances(Model model) {
        List<Attendance> todayAttendances = attendanceService.getTodayAllAttendances();
        model.addAttribute("attendances", todayAttendances);
        model.addAttribute("currentDate", LocalDate.now());

        return "attendance/admin/today";
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }

    @GetMapping("/summary")
    public String showSummary(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestParam(required = false) Integer month,
                              @RequestParam(required = false) Integer year,
                              Model model) {
        User user = userService.findByUsername(userDetails.getUsername());

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        model.addAttribute("user", user);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("workingDays", attendanceService.countWorkingDays(user.getUserId(), month, year));
        model.addAttribute("absentDays", attendanceService.countAbsentDays(user.getUserId(), month, year));
        model.addAttribute("lateDays", attendanceService.countLateDays(user.getUserId(), month, year));
        model.addAttribute("totalHours", attendanceService.sumTotalHours(user.getUserId(), month, year));
        model.addAttribute("overtimeHours", attendanceService.sumOvertimeHours(user.getUserId(), month, year));

        return "attendance/summary";
    }
}