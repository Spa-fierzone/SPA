package com.spazone.controller;

import com.spazone.dto.WorkScheduleDTO;
import com.spazone.dto.WorkScheduleRequest;
import com.spazone.enums.ScheduleStatus;
import com.spazone.service.WorkScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedules")
@CrossOrigin(origins = "*")
public class WorkScheduleController {

    @Autowired
    private WorkScheduleService workScheduleService;

    // Create a new work schedule
    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<WorkScheduleDTO> createSchedule(@RequestBody WorkScheduleRequest request) {
        try {
            WorkScheduleDTO createdSchedule = workScheduleService.createSchedule(request, getCurrentUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing schedule
    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<WorkScheduleDTO> updateSchedule(@PathVariable Integer scheduleId,
                                                         @RequestBody WorkScheduleRequest request) {
        try {
            WorkScheduleDTO updatedSchedule = workScheduleService.updateSchedule(scheduleId, request, getCurrentUserId());
            return ResponseEntity.ok(updatedSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete a schedule
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer scheduleId) {
        try {
            workScheduleService.deleteSchedule(scheduleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get schedule by ID
    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('TECHNICIAN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<WorkScheduleDTO> getScheduleById(@PathVariable Integer scheduleId) {
        try {
            WorkScheduleDTO schedule = workScheduleService.getScheduleById(scheduleId);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get schedules by user ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @workScheduleService.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkScheduleDTO>> getSchedulesByUserId(@PathVariable Integer userId) {
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByUserId(userId);
        return ResponseEntity.ok(schedules);
    }

    // Get schedules by branch ID
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkScheduleDTO>> getSchedulesByBranchId(@PathVariable Integer branchId) {
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByBranchId(branchId);
        return ResponseEntity.ok(schedules);
    }

    // Get schedules by date range
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkScheduleDTO>> getSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByDateRange(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get schedules by user and date range
    @GetMapping("/user/{userId}/date-range")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @workScheduleService.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkScheduleDTO>> getSchedulesByUserAndDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByUserAndDateRange(userId, startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get schedules by branch and date range
    @GetMapping("/branch/{branchId}/date-range")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkScheduleDTO>> getSchedulesByBranchAndDateRange(
            @PathVariable Integer branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByBranchAndDateRange(branchId, startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get technician schedules
    @GetMapping("/technicians")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<WorkScheduleDTO>> getTechnicianSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getTechnicianSchedules(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get receptionist schedules
    @GetMapping("/receptionists")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<WorkScheduleDTO>> getReceptionistSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getReceptionistSchedules(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get manager schedules
    @GetMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WorkScheduleDTO>> getManagerSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<WorkScheduleDTO> schedules = workScheduleService.getManagerSchedules(startDate, endDate);
        return ResponseEntity.ok(schedules);
    }

    // Get weekly schedule for a user
    @GetMapping("/user/{userId}/weekly")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @workScheduleService.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkScheduleDTO>> getWeeklySchedule(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startOfWeek) {
        List<WorkScheduleDTO> schedules = workScheduleService.getWeeklySchedule(userId, startOfWeek);
        return ResponseEntity.ok(schedules);
    }

    // Get monthly schedule for a user
    @GetMapping("/user/{userId}/monthly")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or @workScheduleService.isCurrentUser(#userId)")
    public ResponseEntity<List<WorkScheduleDTO>> getMonthlySchedule(
            @PathVariable Integer userId,
            @RequestParam int year,
            @RequestParam int month) {
        List<WorkScheduleDTO> schedules = workScheduleService.getMonthlySchedule(userId, year, month);
        return ResponseEntity.ok(schedules);
    }

    // Get branch schedule for a specific date
    @GetMapping("/branch/{branchId}/date/{date}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<List<WorkScheduleDTO>> getBranchScheduleByDate(
            @PathVariable Integer branchId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<WorkScheduleDTO> schedules = workScheduleService.getBranchScheduleByDate(branchId, date);
        return ResponseEntity.ok(schedules);
    }

    // Update schedule status
    @PatchMapping("/{scheduleId}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<WorkScheduleDTO> updateScheduleStatus(
            @PathVariable Integer scheduleId,
            @RequestParam ScheduleStatus status) {
        try {
            WorkScheduleDTO updatedSchedule = workScheduleService.updateScheduleStatus(scheduleId, status, getCurrentUserId());
            return ResponseEntity.ok(updatedSchedule);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get current user's schedule for today
    @GetMapping("/my-schedule/today")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('RECEPTIONIST') or hasRole('MANAGER')")
    public ResponseEntity<List<WorkScheduleDTO>> getMyScheduleToday() {
        LocalDate today = LocalDate.now();
        List<WorkScheduleDTO> schedules = workScheduleService.getSchedulesByUserAndDateRange(
            getCurrentUserId(), today, today);
        return ResponseEntity.ok(schedules);
    }

    // Get current user's schedule for this week
    @GetMapping("/my-schedule/week")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('RECEPTIONIST') or hasRole('MANAGER')")
    public ResponseEntity<List<WorkScheduleDTO>> getMyScheduleThisWeek() {
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        List<WorkScheduleDTO> schedules = workScheduleService.getWeeklySchedule(getCurrentUserId(), startOfWeek);
        return ResponseEntity.ok(schedules);
    }

    // Get current user's schedule for this month
    @GetMapping("/my-schedule/month")
    @PreAuthorize("hasRole('TECHNICIAN') or hasRole('RECEPTIONIST') or hasRole('MANAGER')")
    public ResponseEntity<List<WorkScheduleDTO>> getMyScheduleThisMonth() {
        LocalDate now = LocalDate.now();
        List<WorkScheduleDTO> schedules = workScheduleService.getMonthlySchedule(
            getCurrentUserId(), now.getYear(), now.getMonthValue());
        return ResponseEntity.ok(schedules);
    }

    // Helper method to get current user ID (to be implemented based on your security context)
    private Integer getCurrentUserId() {
        // This should be implemented based on your security configuration
        // For now, returning null - you'll need to implement this based on your authentication
        return null;
    }
}
