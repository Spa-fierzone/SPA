package com.spazone.controller;

import com.spazone.dto.TechnicianScheduleDto;
import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.TreatmentRecord;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.BranchService;
import com.spazone.service.ServiceScheduleService;
import com.spazone.service.TreatmentRecordService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/technician/schedule")
public class ServiceScheduleController {

    @Autowired
    private ServiceScheduleService serviceScheduleService;

    @Autowired
    private TreatmentRecordService treatmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BranchService branchService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping
    public String viewSchedule(
            Model model,
            Authentication authentication,
            @RequestParam(value = "serviceName", required = false) String serviceName,
            @RequestParam(value = "branchId", required = false) Integer branchId,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size
    ) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        Integer technicianId = userService.findByUsername(username).getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceSchedule> schedulePage = serviceScheduleService.searchSchedulesForTechnician(
                technicianId, serviceName, branchId, active, pageable);
        // Map each ServiceSchedule to TechnicianScheduleDto
        List<TechnicianScheduleDto> technicianScheduleDtos = schedulePage.getContent().stream().map(schedule -> {
            // Find the appointment for this schedule (if any)
            Appointment appointment = appointmentRepository.findAppointmentsByTechnicianAndDateRange(
                technicianId,
                schedule.getStartTime(),
                schedule.getEndTime()
            ).stream().findFirst().orElse(null);
            // Find the treatment record for this appointment (if any)
            TreatmentRecord treatmentRecord = (appointment != null) ?
                treatmentService.findByAppointmentId(appointment.getAppointmentId()) : null;
            return new TechnicianScheduleDto(schedule, appointment, treatmentRecord);
        }).toList();
        // Pass the list and pagination info to the view
        model.addAttribute("schedulesPage", new org.springframework.data.domain.PageImpl<>(technicianScheduleDtos, pageable, schedulePage.getTotalElements()));
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("branchId", branchId);
        model.addAttribute("active", active);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("branches", branchService.findAllActive());
        // Optionally: add list of branches for filter dropdown
        // model.addAttribute("branches", ...);
        return "technician/schedule";
    }
}