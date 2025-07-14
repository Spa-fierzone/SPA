package com.spazone.controller;

import com.spazone.entity.Membership;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import com.spazone.service.SpaServiceService;
import com.spazone.service.UserService;
import com.spazone.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private MembershipRepository membershipRepository;

    @GetMapping( "/dashboard")
    public String showDashboard(Model model) {
        List<Service> topServices = spaServiceService.getTop6();
        model.addAttribute("topServices", topServices);
        List<User> topTechnicians = userService.findTop3Technicians();
        model.addAttribute("topTechnicians", topTechnicians);
        List<Membership> topMemberships = membershipRepository.findTop3ActiveMembershipsOrderByPrice(PageRequest.of(0, 3));
        model.addAttribute("topMemberships", topMemberships);

        return "dashboard";
    }


    // Nếu bạn muốn redirect tới dashboard khi truy cập root "/"
    @GetMapping("/")
    public String redirectToDashboard() {
        return "redirect:/dashboard";
    }
}
