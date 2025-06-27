package com.spazone.controller;

import com.spazone.dto.TechnicianScheduleDto;
import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.TreatmentRecord;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.ServiceScheduleService;
import com.spazone.service.TreatmentRecordService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping
    public String viewSchedule(Model model, Authentication authentication) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        List<ServiceSchedule> schedules = serviceScheduleService.getSchedulesForTechnician(currentUser);
        List<TechnicianScheduleDto> dtos = schedules.stream().map(schedule -> {
            Appointment appt = appointmentRepository.findClosestMatchingAppointment(currentUser.getUserId(), schedule.getStartTime(), schedule.getEndTime());
            TreatmentRecord record = (appt != null)
                    ? treatmentService.findByAppointmentId(appt.getAppointmentId())
                    : null;
            return new TechnicianScheduleDto(schedule, appt, record);
        }).collect(Collectors.toList());
        model.addAttribute("schedules", dtos);
        return "technician/schedule";
    }
}