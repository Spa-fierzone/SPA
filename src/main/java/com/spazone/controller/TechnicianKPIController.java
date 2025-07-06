package com.spazone.controller;

import com.spazone.entity.UserKPI;
import com.spazone.entity.User;
import com.spazone.service.UserKPIService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/technician/kpi")
public class TechnicianKPIController {

    @Autowired
    private UserKPIService userKPIService;

    @Autowired
    private UserService userService;

    // Hiển thị KPI của technician
    @GetMapping
    public String viewMyKPI(@RequestParam(value = "month", required = false) Integer month,
                           @RequestParam(value = "year", required = false) Integer year,
                           Authentication authentication,
                           Model model) {

        // Lấy thông tin technician hiện tại
        String username = authentication.getName();
        User currentTechnician = userService.findByUsername(username);

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        // Lấy KPI của technician hiện tại
        List<UserKPI> kpis = userKPIService.getKPIsByTechnicianAndMonthAndYear(
            currentTechnician.getUserId(), month, year);

        // Lấy KPI hiện tại (tháng này)
        UserKPI currentKPI = null;
        if (!kpis.isEmpty()) {
            currentKPI = kpis.get(0);
        }

        // Lấy lịch sử KPI (6 tháng gần nhất)
        List<UserKPI> kpiHistory = userKPIService.getKPIHistoryByTechnician(
            currentTechnician.getUserId(), 6);

        model.addAttribute("currentKPI", currentKPI);
        model.addAttribute("kpiHistory", kpiHistory);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("currentTechnician", currentTechnician);

        return "technician/my-kpi";
    }

    // Xem chi tiết KPI của một tháng cụ thể
    @GetMapping("/detail/{kpiId}")
    public String viewKPIDetail(@PathVariable Integer kpiId,
                               Authentication authentication,
                               Model model) {

        String username = authentication.getName();
        User currentTechnician = userService.findByUsername(username);

        UserKPI kpi = userKPIService.getKPIById(kpiId)
            .orElseThrow(() -> new RuntimeException("KPI không tồn tại"));

        // Kiểm tra xem KPI có thuộc về technician hiện tại không
        if (!kpi.getTechnician().getUserId().equals(currentTechnician.getUserId())) {
            throw new RuntimeException("Bạn không có quyền xem KPI này");
        }

        model.addAttribute("kpi", kpi);

        return "technician/kpi-detail";
    }
}
