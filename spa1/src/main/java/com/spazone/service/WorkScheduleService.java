package com.spazone.service;

import com.spazone.dto.WorkScheduleDTO;
import com.spazone.dto.WorkScheduleRequest;
import com.spazone.entity.Branch;
import com.spazone.entity.User;
import com.spazone.entity.WorkSchedule;
import com.spazone.enums.ScheduleStatus;
import com.spazone.enums.ShiftType;
import com.spazone.exception.ResourceNotFoundException;
import com.spazone.repository.BranchRepository;
import com.spazone.repository.UserRepository;
import com.spazone.repository.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkScheduleService {

    @Autowired
    private WorkScheduleRepository workScheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    // Create a new work schedule
    public WorkScheduleDTO createSchedule(WorkScheduleRequest request, Integer createdBy) {
        // Validate user and branch
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Branch branch = branchRepository.findById(request.getBranchId())
            .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        // Check for schedule conflicts
        List<WorkSchedule> conflicts = workScheduleRepository.findConflictingSchedules(
            request.getUserId(), request.getWorkDate(), request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Schedule conflict detected for user " + user.getFullName() +
                                     " on " + request.getWorkDate());
        }

        // Create the schedule
        WorkSchedule schedule = new WorkSchedule();
        schedule.setUser(user);
        schedule.setBranch(branch);
        schedule.setWorkDate(request.getWorkDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setShiftType(request.getShiftType());
        schedule.setStatus(request.getStatus());
        schedule.setBreakStart(request.getBreakStart());
        schedule.setBreakEnd(request.getBreakEnd());
        schedule.setNotes(request.getNotes());
        schedule.setIsRecurring(request.getIsRecurring());
        schedule.setRecurringPattern(request.getRecurringPattern());
        schedule.setRecurringEndDate(request.getRecurringEndDate());
        schedule.setCreatedBy(createdBy);

        WorkSchedule savedSchedule = workScheduleRepository.save(schedule);

        // Handle recurring schedules
        if (request.getIsRecurring() && request.getRecurringPattern() != null) {
            createRecurringSchedules(savedSchedule, request, createdBy);
        }

        return convertToDTO(savedSchedule);
    }

    // Create recurring schedules
    private void createRecurringSchedules(WorkSchedule originalSchedule, WorkScheduleRequest request, Integer createdBy) {
        LocalDate currentDate = request.getWorkDate();
        LocalDate endDate = request.getRecurringEndDate();

        if (endDate == null) {
            // Default to 3 months if no end date specified
            endDate = currentDate.plusMonths(3);
        }

        while (currentDate.isBefore(endDate)) {
            currentDate = getNextRecurringDate(currentDate, request.getRecurringPattern());

            if (currentDate.isAfter(endDate)) {
                break;
            }

            // Check for conflicts before creating
            List<WorkSchedule> conflicts = workScheduleRepository.findConflictingSchedules(
                request.getUserId(), currentDate, request.getStartTime(), request.getEndTime());

            if (conflicts.isEmpty()) {
                WorkSchedule recurringSchedule = new WorkSchedule();
                recurringSchedule.setUser(originalSchedule.getUser());
                recurringSchedule.setBranch(originalSchedule.getBranch());
                recurringSchedule.setWorkDate(currentDate);
                recurringSchedule.setStartTime(request.getStartTime());
                recurringSchedule.setEndTime(request.getEndTime());
                recurringSchedule.setShiftType(request.getShiftType());
                recurringSchedule.setStatus(ScheduleStatus.SCHEDULED);
                recurringSchedule.setBreakStart(request.getBreakStart());
                recurringSchedule.setBreakEnd(request.getBreakEnd());
                recurringSchedule.setNotes(request.getNotes());
                recurringSchedule.setIsRecurring(true);
                recurringSchedule.setRecurringPattern(request.getRecurringPattern());
                recurringSchedule.setRecurringEndDate(request.getRecurringEndDate());
                recurringSchedule.setCreatedBy(createdBy);

                workScheduleRepository.save(recurringSchedule);
            }
        }
    }

    // Get next recurring date based on pattern
    private LocalDate getNextRecurringDate(LocalDate currentDate, String pattern) {
        return switch (pattern.toUpperCase()) {
            case "DAILY" -> currentDate.plusDays(1);
            case "WEEKLY" -> currentDate.plusWeeks(1);
            case "MONTHLY" -> currentDate.plusMonths(1);
            default -> currentDate.plusDays(1);
        };
    }

    // Update an existing schedule
    public WorkScheduleDTO updateSchedule(Integer scheduleId, WorkScheduleRequest request, Integer updatedBy) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        // Check for conflicts if time or date is changed
        if (!schedule.getWorkDate().equals(request.getWorkDate()) ||
            !schedule.getStartTime().equals(request.getStartTime()) ||
            !schedule.getEndTime().equals(request.getEndTime())) {

            List<WorkSchedule> conflicts = workScheduleRepository.findConflictingSchedules(
                request.getUserId(), request.getWorkDate(), request.getStartTime(), request.getEndTime());

            // Remove current schedule from conflicts
            conflicts.removeIf(ws -> ws.getScheduleId().equals(scheduleId));

            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Schedule conflict detected");
            }
        }

        // Update schedule
        schedule.setWorkDate(request.getWorkDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setShiftType(request.getShiftType());
        schedule.setStatus(request.getStatus());
        schedule.setBreakStart(request.getBreakStart());
        schedule.setBreakEnd(request.getBreakEnd());
        schedule.setNotes(request.getNotes());
        schedule.setUpdatedBy(updatedBy);

        WorkSchedule updatedSchedule = workScheduleRepository.save(schedule);
        return convertToDTO(updatedSchedule);
    }

    // Delete a schedule
    public void deleteSchedule(Integer scheduleId) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        workScheduleRepository.delete(schedule);
    }

    // Get schedule by ID
    public WorkScheduleDTO getScheduleById(Integer scheduleId) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        return convertToDTO(schedule);
    }

    // Get schedules by user ID
    public List<WorkScheduleDTO> getSchedulesByUserId(Integer userId) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserUserId(userId);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by branch ID
    public List<WorkScheduleDTO> getSchedulesByBranchId(Integer branchId) {
        List<WorkSchedule> schedules = workScheduleRepository.findByBranchBranchId(branchId);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by date range
    public List<WorkScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByWorkDateBetween(startDate, endDate);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by user and date range
    public List<WorkScheduleDTO> getSchedulesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserUserIdAndWorkDateBetween(userId, startDate, endDate);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by branch and date range
    public List<WorkScheduleDTO> getSchedulesByBranchAndDateRange(Integer branchId, LocalDate startDate, LocalDate endDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByBranchBranchIdAndWorkDateBetween(branchId, startDate, endDate);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by role
    public List<WorkScheduleDTO> getSchedulesByRole(String roleName) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserRole(roleName);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get schedules by role and date range
    public List<WorkScheduleDTO> getSchedulesByRoleAndDateRange(String roleName, LocalDate startDate, LocalDate endDate) {
        List<WorkSchedule> schedules = workScheduleRepository.findByUserRoleAndDateRange(roleName, startDate, endDate);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get technician schedules
    public List<WorkScheduleDTO> getTechnicianSchedules(LocalDate startDate, LocalDate endDate) {
        return getSchedulesByRoleAndDateRange("TECHNICIAN", startDate, endDate);
    }

    // Get receptionist schedules
    public List<WorkScheduleDTO> getReceptionistSchedules(LocalDate startDate, LocalDate endDate) {
        return getSchedulesByRoleAndDateRange("RECEPTIONIST", startDate, endDate);
    }

    // Get manager schedules
    public List<WorkScheduleDTO> getManagerSchedules(LocalDate startDate, LocalDate endDate) {
        return getSchedulesByRoleAndDateRange("MANAGER", startDate, endDate);
    }

    // Get weekly schedule for a user
    public List<WorkScheduleDTO> getWeeklySchedule(Integer userId, LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        List<WorkSchedule> schedules = workScheduleRepository.findWeeklySchedule(userId, startOfWeek, endOfWeek);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get monthly schedule for a user
    public List<WorkScheduleDTO> getMonthlySchedule(Integer userId, int year, int month) {
        List<WorkSchedule> schedules = workScheduleRepository.findMonthlySchedule(userId, year, month);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Get branch schedule for a specific date
    public List<WorkScheduleDTO> getBranchScheduleByDate(Integer branchId, LocalDate date) {
        List<WorkSchedule> schedules = workScheduleRepository.findBranchScheduleByDate(branchId, date);
        return schedules.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Update schedule status
    public WorkScheduleDTO updateScheduleStatus(Integer scheduleId, ScheduleStatus newStatus, Integer updatedBy) {
        WorkSchedule schedule = workScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        schedule.setStatus(newStatus);
        schedule.setUpdatedBy(updatedBy);

        WorkSchedule updatedSchedule = workScheduleRepository.save(schedule);
        return convertToDTO(updatedSchedule);
    }

    // Convert WorkSchedule entity to DTO
    private WorkScheduleDTO convertToDTO(WorkSchedule schedule) {
        WorkScheduleDTO dto = new WorkScheduleDTO();
        dto.setScheduleId(schedule.getScheduleId());
        dto.setUserId(schedule.getUser().getUserId());
        dto.setUserFullName(schedule.getUser().getFullName());
        dto.setUserRole(schedule.getUser().getRoles().iterator().next().getRoleName()); // Get first role
        dto.setBranchId(schedule.getBranch().getBranchId());
        dto.setBranchName(schedule.getBranch().getName());
        dto.setWorkDate(schedule.getWorkDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setShiftType(schedule.getShiftType());
        dto.setStatus(schedule.getStatus());
        dto.setBreakStart(schedule.getBreakStart());
        dto.setBreakEnd(schedule.getBreakEnd());
        dto.setNotes(schedule.getNotes());
        dto.setIsRecurring(schedule.getIsRecurring());
        dto.setRecurringPattern(schedule.getRecurringPattern());
        dto.setRecurringEndDate(schedule.getRecurringEndDate());

        return dto;
    }
}
