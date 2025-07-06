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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/kpi")
public class AdminKPIController {

    @Autowired
    private UserKPIService userKPIService;

    @Autowired
    private UserService userService;

    // Hiển thị danh sách KPI
    @GetMapping
    public String listKPIs(@RequestParam(value = "month", required = false) Integer month,
                          @RequestParam(value = "year", required = false) Integer year,
                          Authentication authentication,
                          Model model) {

        // Lấy thông tin manager hiện tại
        String username = authentication.getName();
        User currentManager = userService.findByUsername(username);

        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        List<UserKPI> kpis = userKPIService.getKPIsByManagerAndMonthAndYear(currentManager.getUserId(), month, year);

        model.addAttribute("kpis", kpis);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("currentManager", currentManager);

        return "admin/kpi-management";
    }

    // Hiển thị form tạo KPI mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<User> technicians = userKPIService.getAllTechnicians();

        LocalDate now = LocalDate.now();

        model.addAttribute("technicians", technicians);
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());

        return "admin/kpi-form";
    }

    // Tạo KPI mới
    @PostMapping("/create")
    public String createKPI(@RequestParam Integer technicianId,
                           @RequestParam Integer month,
                           @RequestParam Integer year,
                           @RequestParam Integer targetAppointments,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        try {
            String username = authentication.getName();
            User currentManager = userService.findByUsername(username);

            // Kiểm tra KPI đã tồn tại chưa
            if (userKPIService.isKPIExists(technicianId, month, year)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                    "KPI cho technician này trong tháng " + month + "/" + year + " đã tồn tại!");
                return "redirect:/manager/kpi/create";
            }

            UserKPI kpi = userKPIService.createOrUpdateKPI(technicianId, currentManager.getUserId(), month, year, targetAppointments);

            redirectAttributes.addFlashAttribute("successMessage", "Tạo KPI thành công!");
            return "redirect:/manager/kpi";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/manager/kpi/create";
        }
    }

    // Hiển thị form chỉnh sửa KPI
    @GetMapping("/edit/{kpiId}")
    public String showEditForm(@PathVariable Integer kpiId, Model model, RedirectAttributes redirectAttributes) {
        Optional<UserKPI> kpi = userKPIService.getKPIById(kpiId);

        if (kpi.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "KPI không tồn tại!");
            return "redirect:/manager/kpi";
        }

        List<User> technicians = userKPIService.getAllTechnicians();

        model.addAttribute("kpi", kpi.get());
        model.addAttribute("technicians", technicians);

        return "admin/kpi-edit";
    }

    // Cập nhật KPI
    @PostMapping("/update/{kpiId}")
    public String updateKPI(@PathVariable Integer kpiId,
                           @RequestParam Integer targetAppointments,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        try {
            Optional<UserKPI> existingKPI = userKPIService.getKPIById(kpiId);
            if (existingKPI.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "KPI không tồn tại!");
                return "redirect:/manager/kpi";
            }

            String username = authentication.getName();
            User currentManager = userService.findByUsername(username);

            UserKPI kpi = existingKPI.get();
            userKPIService.createOrUpdateKPI(
                kpi.getTechnician().getUserId(),
                currentManager.getUserId(),
                kpi.getMonth(),
                kpi.getYear(),
                targetAppointments
            );

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật KPI thành công!");
            return "redirect:/manager/kpi";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/manager/kpi";
        }
    }

    // Xóa KPI
    @PostMapping("/delete/{kpiId}")
    public String deleteKPI(@PathVariable Integer kpiId, RedirectAttributes redirectAttributes) {
        try {
            userKPIService.deleteKPI(kpiId);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa KPI thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa KPI: " + e.getMessage());
        }

        return "redirect:/manager/kpi";
    }

    // Cập nhật actual appointments cho tất cả KPI trong tháng
    @PostMapping("/update-actual")
    public String updateActualAppointments(@RequestParam Integer month,
                                          @RequestParam Integer year,
                                          RedirectAttributes redirectAttributes) {
        try {
            userKPIService.updateActualAppointments(month, year);
            redirectAttributes.addFlashAttribute("successMessage",
                "Cập nhật số lịch hẹn thực tế cho tháng " + month + "/" + year + " thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/manager/kpi?month=" + month + "&year=" + year;
    }

    // Xem chi tiết KPI
    @GetMapping("/detail/{kpiId}")
    public String viewKPIDetail(@PathVariable Integer kpiId, Model model, RedirectAttributes redirectAttributes) {
        Optional<UserKPI> kpi = userKPIService.getKPIById(kpiId);

        if (kpi.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "KPI không tồn tại!");
            return "redirect:/admin/kpi";
        }

        model.addAttribute("kpi", kpi.get());

        return "admin/kpi-detail";
    }

    // Thay đổi trạng thái KPI
    @PostMapping("/change-status/{kpiId}")
    public String changeKPIStatus(@PathVariable Integer kpiId,
                                 @RequestParam String status,
                                 RedirectAttributes redirectAttributes) {
        try {
            userKPIService.updateKPIStatus(kpiId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái KPI thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/manager/kpi";
    }
}
