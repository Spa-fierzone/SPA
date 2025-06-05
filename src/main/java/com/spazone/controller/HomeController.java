package com.spazone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/dashboard"})
    public String showDashboard() {
        return "dashboard";
    }

    // Nếu bạn muốn redirect tới dashboard khi truy cập root "/"
    // @GetMapping("/")
    // public String redirectToDashboard() {
    //     return "redirect:/dashboard";
    // }
}
