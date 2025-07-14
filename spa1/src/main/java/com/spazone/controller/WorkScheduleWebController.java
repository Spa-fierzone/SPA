package com.spazone.controller;

import com.spazone.dto.WorkScheduleDTO;
import com.spazone.dto.WorkScheduleRequest;
import com.spazone.entity.User;
import com.spazone.enums.ShiftType;
import com.spazone.enums.ScheduleStatus;
import com.spazone.service.WorkScheduleService;
import com.spazone.service.UserService;
import com.spazone.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/schedules")
public class WorkScheduleWebController {

    @Autowired
    private WorkScheduleService workScheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    // Calendar view for managers/admins
    @GetMapping("/calendar")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String showCalendarView(Model model,
                                  @RequestParam(required = false) String date,
                                  @RequestParam(required = false) Integer branchId) {
        LocalDate viewDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        // Get schedules for the current month
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByDateRange(
            viewDate.withDayOfMonth(1),
            viewDate.withDayOfMonth(viewDate.lengthOfMonth())
        );

        // Filter by branch if specified
        if (branchId != null) {
            schedules = schedules.stream()
                .filter(s -> s.getBranchId().equals(branchId))
                .toList();
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("currentDate", viewDate);
        model.addAttribute("branches", branchService.getAllBranches());
        model.addAttribute("selectedBranchId", branchId);

        return "admin/work-schedule-calendar";
    }

    // Schedule management page
    @GetMapping("/manage")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String showScheduleManagement(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<WorkScheduleDTO> weeklySchedules = workScheduleService.getSchedulesByDateRange(weekStart, weekEnd);

        model.addAttribute("weeklySchedules", weeklySchedules);
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("users", userService.getAllUsers()); // Use correct method name
        model.addAttribute("branches", branchService.getAllBranches()); // Use correct method name

        return "admin/work-schedule-management";
    }

    // Create schedule form
    @GetMapping("/create")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String showCreateScheduleForm(Model model) {
        model.addAttribute("users", userService.getAllUsers()); // Use correct method name
        model.addAttribute("branches", branchService.getAllBranches()); // Use correct method name
        return "admin/work-schedule-form";
    }

    // Edit schedule form
    @GetMapping("/edit/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String showEditScheduleForm(@PathVariable Integer scheduleId, Model model) {
        WorkScheduleDTO schedule = workScheduleService.getScheduleById(scheduleId);

        model.addAttribute("schedule", schedule);
        model.addAttribute("users", userService.getAllUsers()); // Use correct method name
        model.addAttribute("branches", branchService.getAllBranches()); // Use correct method name

        return "admin/work-schedule-form";
    }

    // Technician schedule view
    @GetMapping("/technician")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String showTechnicianSchedules(Model model,
                                        @RequestParam(required = false) String startDate,
                                        @RequestParam(required = false) String endDate) {
        LocalDate start, end;

        if (startDate == null || startDate.isEmpty()) {
            start = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        } else {
            start = LocalDate.parse(startDate);
        }

        if (endDate == null || endDate.isEmpty()) {
            end = start.plusDays(6);
        } else {
            end = LocalDate.parse(endDate);
        }

        List<WorkScheduleDTO> technicianSchedules = workScheduleService.getTechnicianSchedules(start, end);

        long completedSchedules = technicianSchedules.stream()
                .filter(s -> s.getStatus() == com.spazone.enums.ScheduleStatus.COMPLETED)
                .count();

        Map<LocalDate, List<WorkScheduleDTO>> schedulesByDate = technicianSchedules.stream()
                .collect(Collectors.groupingBy(WorkScheduleDTO::getWorkDate));

        List<LocalDate> sortedDates = schedulesByDate.keySet().stream().sorted().toList();

        model.addAttribute("schedulesByDate", schedulesByDate);
        model.addAttribute("sortedDates", sortedDates);
        model.addAttribute("schedules", technicianSchedules);
        model.addAttribute("completedSchedules", completedSchedules);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("pageTitle", "Lịch làm việc Kỹ thuật viên");

        return "technician/work-schedule";
    }

    // Receptionist schedule view
    @GetMapping("/receptionist")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('MANAGER') or hasRole('ADMIN')")
    public String showReceptionistSchedules(Model model,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : start.plusDays(6);

        List<WorkScheduleDTO> receptionistSchedules = workScheduleService.getReceptionistSchedules(start, end);

        model.addAttribute("schedules", receptionistSchedules);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("pageTitle", "Lịch làm việc Lễ tân");

        return "reception/work-schedule";
    }

    // My schedule view (for individual employees)
    @GetMapping("/my-schedule")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('RECEPTIONIST') or hasRole('MANAGER')")
    public String showMySchedule(Model model,
                                 @RequestParam(required = false) String view,
                                 @RequestParam(required = false) String date,
                                 Authentication authentication) {
        String viewType = view != null ? view : "week";
        LocalDate viewDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        List<WorkScheduleDTO> mySchedules;
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);
        Integer currentUserId = currentUser != null ? currentUser.getUserId() : null;

        switch (viewType) {
            case "day":
                mySchedules = workScheduleService.getSchedulesByUserAndDateRange(currentUserId, viewDate, viewDate);
                break;
            case "month":
                mySchedules = workScheduleService.getMonthlySchedule(currentUserId, viewDate.getYear(), viewDate.getMonthValue());
                break;
            default: // week
                LocalDate weekStart = viewDate.with(java.time.DayOfWeek.MONDAY);
                mySchedules = workScheduleService.getWeeklySchedule(currentUserId, weekStart);
                break;
        }

        // Calculate statistics for template
        long upcomingSchedulesCount = mySchedules.stream()
            .filter(s -> s.getStatus() == ScheduleStatus.SCHEDULED)
            .count();

        long totalWorkingHours = mySchedules.stream()
            .mapToLong(WorkScheduleDTO::getScheduledHours)
            .sum();

        long distinctBranchesCount = mySchedules.stream()
            .map(WorkScheduleDTO::getBranchName)
            .distinct()
            .count();

        // Check for today's schedules
        LocalDate today = LocalDate.now();
        boolean hasTodaySchedules = mySchedules.stream()
            .anyMatch(s -> s.getWorkDate().equals(today) && s.getStatus() == ScheduleStatus.SCHEDULED);

        long todaySchedulesCount = mySchedules.stream()
            .filter(s -> s.getWorkDate().equals(today))
            .count();

        long completedSchedulesCount = mySchedules.stream()
            .filter(s -> s.getStatus() == ScheduleStatus.COMPLETED)
            .count();

        // Group schedules by date for template
        Map<LocalDate, List<WorkScheduleDTO>> schedulesByDate = mySchedules.stream()
            .collect(Collectors.groupingBy(WorkScheduleDTO::getWorkDate));

        // Get sorted dates for template
        List<LocalDate> sortedDates = mySchedules.stream()
            .map(WorkScheduleDTO::getWorkDate)
            .distinct()
            .sorted()
            .collect(Collectors.toList());

        model.addAttribute("schedules", mySchedules);
        model.addAttribute("viewType", viewType);
        model.addAttribute("viewDate", viewDate);
        model.addAttribute("pageTitle", "Lịch làm việc của tôi");

        // Add calculated statistics
        model.addAttribute("upcomingSchedulesCount", upcomingSchedulesCount);
        model.addAttribute("totalWorkingHours", totalWorkingHours);
        model.addAttribute("distinctBranchesCount", distinctBranchesCount);
        model.addAttribute("hasTodaySchedules", hasTodaySchedules);
        model.addAttribute("todaySchedulesCount", todaySchedulesCount);
        model.addAttribute("completedSchedulesCount", completedSchedulesCount);
        model.addAttribute("schedulesByDate", schedulesByDate);
        model.addAttribute("sortedDates", sortedDates);

        return "profile/my-schedule";
    }

    // Branch daily schedule view
    @GetMapping("/branch/{branchId}/daily")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public String showBranchDailySchedule(@PathVariable Integer branchId, Model model,
                                        @RequestParam(required = false) String date) {
        LocalDate viewDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        List<WorkScheduleDTO> dailySchedules = workScheduleService.getBranchScheduleByDate(branchId, viewDate);

        model.addAttribute("schedules", dailySchedules);
        model.addAttribute("viewDate", viewDate);
        model.addAttribute("branchId", branchId);
        model.addAttribute("branch", branchService.findById(branchId)); // Use existing method instead of getBranchById
        model.addAttribute("pageTitle", "Lịch làm việc hàng ngày");

        return "admin/branch-daily-schedule";
    }

    // Bulk create schedules for multiple employees
    @PostMapping("/bulk-create")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String bulkCreateSchedules(@RequestParam List<Integer> userIds,
                                    @RequestParam Integer branchId,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate,
                                    @RequestParam String shiftType,
                                    @RequestParam String startTime,
                                    @RequestParam String endTime,
                                    Model model) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            for (Integer userId : userIds) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    // Skip weekends if needed (optional logic)
                    if (date.getDayOfWeek().getValue() <= 5) { // Monday to Friday only
                        WorkScheduleRequest request = new WorkScheduleRequest();
                        request.setUserId(userId);
                        request.setBranchId(branchId);
                        request.setWorkDate(date);
                        request.setStartTime(LocalTime.parse(startTime));
                        request.setEndTime(LocalTime.parse(endTime));
                        request.setShiftType(ShiftType.valueOf(shiftType));

                        workScheduleService.createSchedule(request, getCurrentUserId());
                    }
                }
            }

            model.addAttribute("successMessage", "Bulk schedules created successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error creating bulk schedules: " + e.getMessage());
        }

        return "redirect:/schedules/manage";
    }

    // Get schedule coverage report for a branch
    @GetMapping("/coverage-report/{branchId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String getScheduleCoverageReport(@PathVariable Integer branchId,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          Model model) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : start.plusDays(6);

        // Get all schedules for the branch in the date range
        List<WorkScheduleDTO> allSchedules = workScheduleService.getSchedulesByBranchAndDateRange(branchId, start, end);

        // Group by role
        Map<String, List<WorkScheduleDTO>> schedulesByRole = allSchedules.stream()
            .collect(Collectors.groupingBy(WorkScheduleDTO::getUserRole));

        // Calculate coverage statistics
        Map<String, Long> coverageStats = new HashMap<>();
        coverageStats.put("totalSchedules", (long) allSchedules.size());
        coverageStats.put("technicianSchedules", (long) schedulesByRole.getOrDefault("TECHNICIAN", new ArrayList<>()).size());
        coverageStats.put("receptionistSchedules", (long) schedulesByRole.getOrDefault("RECEPTIONIST", new ArrayList<>()).size());
        coverageStats.put("managerSchedules", (long) schedulesByRole.getOrDefault("MANAGER", new ArrayList<>()).size());

        model.addAttribute("schedules", allSchedules);
        model.addAttribute("schedulesByRole", schedulesByRole);
        model.addAttribute("coverageStats", coverageStats);
        model.addAttribute("branch", branchService.findById(branchId));
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("pageTitle", "Schedule Coverage Report");

        return "admin/schedule-coverage-report";
    }

    // Copy schedule from one week to another
    @PostMapping("/copy-week")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String copyWeekSchedule(@RequestParam String sourceWeekStart,
                                 @RequestParam String targetWeekStart,
                                 @RequestParam(required = false) Integer branchId,
                                 Model model) {
        try {
            LocalDate sourceStart = LocalDate.parse(sourceWeekStart);
            LocalDate targetStart = LocalDate.parse(targetWeekStart);
            LocalDate sourceEnd = sourceStart.plusDays(6);

            // Get source week schedules
            List<WorkScheduleDTO> sourceSchedules;
            if (branchId != null) {
                sourceSchedules = workScheduleService.getSchedulesByBranchAndDateRange(branchId, sourceStart, sourceEnd);
            } else {
                sourceSchedules = workScheduleService.getSchedulesByDateRange(sourceStart, sourceEnd);
            }

            // Create new schedules for target week
            long daysDifference = ChronoUnit.DAYS.between(sourceStart, targetStart);

            for (WorkScheduleDTO sourceSchedule : sourceSchedules) {
                WorkScheduleRequest newRequest = new WorkScheduleRequest();
                newRequest.setUserId(sourceSchedule.getUserId());
                newRequest.setBranchId(sourceSchedule.getBranchId());
                newRequest.setWorkDate(sourceSchedule.getWorkDate().plusDays(daysDifference));
                newRequest.setStartTime(sourceSchedule.getStartTime());
                newRequest.setEndTime(sourceSchedule.getEndTime());
                newRequest.setShiftType(sourceSchedule.getShiftType());
                newRequest.setBreakStart(sourceSchedule.getBreakStart());
                newRequest.setBreakEnd(sourceSchedule.getBreakEnd());
                newRequest.setNotes("Copied from " + sourceSchedule.getWorkDate());

                workScheduleService.createSchedule(newRequest, getCurrentUserId());
            }

            model.addAttribute("successMessage", "Week schedule copied successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error copying schedule: " + e.getMessage());
        }

        return "redirect:/schedules/manage";
    }

    // Get schedule conflicts report
    @GetMapping("/conflicts")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String getScheduleConflicts(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate weekEnd = today.plusDays(7);

        // This would require a custom service method to detect conflicts
        // For now, we'll return a placeholder
        List<WorkScheduleDTO> upcomingSchedules = workScheduleService.getSchedulesByDateRange(today, weekEnd);

        model.addAttribute("schedules", upcomingSchedules);
        model.addAttribute("pageTitle", "Schedule Conflicts");

        return "admin/schedule-conflicts";
    }

    // Export schedule to CSV
    @GetMapping("/export")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<String> exportSchedulesToCSV(@RequestParam String startDate,
                                                      @RequestParam String endDate,
                                                      @RequestParam(required = false) Integer branchId) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<WorkScheduleDTO> schedules;
        if (branchId != null) {
            schedules = workScheduleService.getSchedulesByBranchAndDateRange(branchId, start, end);
        } else {
            schedules = workScheduleService.getSchedulesByDateRange(start, end);
        }

        StringBuilder csv = new StringBuilder();
        csv.append("Date,Employee,Role,Branch,Shift Type,Start Time,End Time,Status\n");

        for (WorkScheduleDTO schedule : schedules) {
            csv.append(schedule.getWorkDate()).append(",")
               .append(schedule.getUserFullName()).append(",")
               .append(schedule.getUserRole()).append(",")
               .append(schedule.getBranchName()).append(",")
               .append(schedule.getShiftTypeDisplayName()).append(",")
               .append(schedule.getStartTime()).append(",")
               .append(schedule.getEndTime()).append(",")
               .append(schedule.getStatusDisplayName()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "schedules_" + start + "_to_" + end + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csv.toString());
    }

    // Get employee availability
    @GetMapping("/availability/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public String getEmployeeAvailability(@PathVariable Integer userId,
                                        @RequestParam(required = false) String date,
                                        Model model) {
        LocalDate viewDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        LocalDate weekStart = viewDate.with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<WorkScheduleDTO> userSchedules = workScheduleService.getSchedulesByUserAndDateRange(userId, weekStart, weekEnd);

        // Calculate availability (hours when not scheduled)
        Map<LocalDate, List<String>> availability = new HashMap<>();
        for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
            availability.put(d, calculateAvailableHours(userSchedules, d));
        }

        model.addAttribute("userSchedules", userSchedules);
        model.addAttribute("availability", availability);
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("weekStart", weekStart);
        model.addAttribute("weekEnd", weekEnd);
        model.addAttribute("pageTitle", "Employee Availability");

        return "admin/employee-availability";
    }

    // Helper method to calculate available hours for a given date
    private List<String> calculateAvailableHours(List<WorkScheduleDTO> schedules, LocalDate date) {
        List<String> availableHours = new ArrayList<>();
        List<WorkScheduleDTO> daySchedules = schedules.stream()
            .filter(s -> s.getWorkDate().equals(date))
            .toList();

        // Simple logic: if no schedule, available all day
        if (daySchedules.isEmpty()) {
            availableHours.add("08:00-17:00"); // Default business hours
        } else {
            // More complex logic could be implemented here
            availableHours.add("Available between scheduled shifts");
        }

        return availableHours;
    }

    // Helper method to get current user ID (implement based on your security setup)
    private Integer getCurrentUserId() {
        // This should be implemented based on your security configuration
        return null;
    }
}
