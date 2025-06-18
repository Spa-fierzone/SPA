package com.spazone.controller;

import com.spazone.entity.Appointment;
import com.spazone.entity.AppointmentHandled;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentHandledRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reception")
public class ReceptionController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentHandledRepository appointmentHandledRepository;

    @Autowired
    private UserRepository userRepository;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/appointments")
    public String viewTodayAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getTodayAppointments());
        return "reception/appointments";
    }

    @PostMapping("/appointments/{id}/checkin")
    public String checkIn(@PathVariable Integer id, Authentication authentication) {
        Appointment appointment = appointmentService.checkIn(id);
        String username = authentication.getName();
        User receptionist = userRepository.findByUsername(username).orElse(null);

        if (appointment != null && receptionist != null) {
            AppointmentHandled handled = new AppointmentHandled();
            handled.setAppointment(appointment);
            handled.setReceptionist(receptionist);
            handled.setHandledAt(LocalDateTime.now());
            appointmentHandledRepository.save(handled);
        }

        return "redirect:/reception/appointments";
    }

    @PostMapping("/appointments/{id}/checkout")
    public String checkOut(@PathVariable Integer id) {
        appointmentService.checkOut(id);
        return "redirect:/reception/appointments";
    }
}

