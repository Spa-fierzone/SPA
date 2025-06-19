package com.spazone.controller;

import com.spazone.entity.Service;
import com.spazone.service.SpaServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/services")
public class SpaServiceController {

    @Autowired
    private SpaServiceService spaServiceService;

    @GetMapping
    public String listServices(Model model) {
        List<Service> allServices = spaServiceService.findAllActive();
        model.addAttribute("services", allServices);
        return "spa-service/list";
    }

    @GetMapping("/detail/{id}")
    public String viewServiceDetail(@PathVariable("id") Integer id, Model model) {
        Service service = spaServiceService.getById(id);
        if (service == null || !"active".equalsIgnoreCase(service.getStatus())) {
            return "redirect:/services";
        }
        model.addAttribute("service", service);
        return "spa-service/detail";
    }
}
