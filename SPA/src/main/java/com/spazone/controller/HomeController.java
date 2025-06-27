package com.spazone.controller;

import com.spazone.entity.Service;
import com.spazone.entity.User;
import com.spazone.service.SpaServiceService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    private UserService userService;

    @GetMapping( "/dashboard")
    public String showDashboard(Model model) {
        List<Service> topServices = spaServiceService.getTop6();
        model.addAttribute("topServices", topServices);
        List<User> topTechnicians = userService.findTop3Technicians();
        model.addAttribute("topTechnicians", topTechnicians);

        return "dashboard";
    }


    // Nếu bạn muốn redirect tới dashboard khi truy cập root "/"
    // @GetMapping("/")
    // public String redirectToDashboard() {
    //     return "redirect:/dashboard";
    // }
}
