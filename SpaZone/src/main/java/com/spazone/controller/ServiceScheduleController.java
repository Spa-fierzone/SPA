package com.spazone.controller;

import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import com.spazone.repository.UserRepository;
import com.spazone.service.ServiceScheduleService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/technician/schedule")
public class ServiceScheduleController {

    @Autowired
    private ServiceScheduleService serviceScheduleService;


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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
        model.addAttribute("schedules", schedules);
        return "technician/schedule";
    }
}