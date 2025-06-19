package com.spazone.controller;

import com.spazone.entity.Service;
import com.spazone.service.SpaServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SpaServiceService spaServiceService;

    @GetMapping({"/", "/dashboard"})
    public String showDashboard(Model model) {
        List<Service> topServices = spaServiceService.getTop6();
        model.addAttribute("topServices", topServices);
        return "dashboard";
    }

    // Nếu bạn muốn redirect tới dashboard khi truy cập root "/"
    // @GetMapping("/")
    // public String redirectToDashboard() {
    //     return "redirect:/dashboard";
    // }
}
