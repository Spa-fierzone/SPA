package com.spazone.controller;

import com.spazone.entity.SalarySetting;
import com.spazone.service.impl.SalarySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/salary-settings")
public class SalarySettingController {

    @Autowired
    private SalarySettingService salarySettingService;

    @GetMapping
    public String showSettings(Model model) {
        model.addAttribute("receptionistSetting",
                salarySettingService.findByRole(SalarySetting.Role.RECEPTIONIST).orElse(new SalarySetting(null, SalarySetting.Role.RECEPTIONIST, 0.0, null, 0.0, 0.0, 0.0, 0.0)));
        model.addAttribute("technicianSetting",
                salarySettingService.findByRole(SalarySetting.Role.TECHNICIAN).orElse(new SalarySetting(null, SalarySetting.Role.TECHNICIAN, 0.0, null, 0.0, 0.0, 0.0, 0.0)));
        return "admin/salary-settings";
    }

    @PostMapping("/save")
    public String saveSettings(@ModelAttribute("receptionistSetting") SalarySetting receptionistSetting,
                               @ModelAttribute("technicianSetting") SalarySetting technicianSetting) {

        receptionistSetting.setRole(SalarySetting.Role.RECEPTIONIST);
        technicianSetting.setRole(SalarySetting.Role.TECHNICIAN);

        salarySettingService.save(receptionistSetting);
        salarySettingService.save(technicianSetting);
        return "redirect:/admin/salary-settings";
    }
}
