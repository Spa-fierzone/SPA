package com.spazone.controller;

import com.spazone.dto.SalaryCalculationDto;
import com.spazone.entity.Branch;
import com.spazone.entity.SalaryRecord;
import com.spazone.entity.User;
import com.spazone.repository.BranchRepository;
import com.spazone.service.UserService;
import com.spazone.service.impl.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/calculate")
    public String showSalaryCalculation(Model model,
                                        @RequestParam(value = "month", required = false) Integer month,
                                        @RequestParam(value = "year", required = false) Integer year,
                                        @RequestParam(value = "branchId", required = false) Integer branchId) {

        // Set default values if not provided
        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        // Load branches for dropdown
        List<Branch> branches = branchRepository.findByIsActiveTrue();

        // Calculate salaries
        List<SalaryCalculationDto> calculations = salaryService.calculateMonthlySalary(month, year, branchId);

        model.addAttribute("calculations", calculations);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("monthName", getMonthName(month));

        return "salary/calculate";
    }

    @GetMapping("/admin/user/{userId}/set-base-salary")
    public String showSetBaseSalaryForm(@PathVariable Integer userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "salary/set-base-salary";
    }

    @PostMapping("/admin/user/{userId}/set-base-salary")
    public String setBaseSalary(@PathVariable Integer userId,
                                @RequestParam("baseSalary") BigDecimal baseSalary,
                                Model model) {
        User user = userService.getUserById(userId);
        user.setBaseSalary(baseSalary);
        userService.saveUser(user);

        model.addAttribute("success", "Lương cơ bản đã được cập nhật thành công!");
        return "redirect:/salary/calculate";
    }

    @PostMapping("/save")
    public String saveSalaryRecords(@RequestParam("month") Integer month,
                                    @RequestParam("year") Integer year,
                                    @RequestParam(value = "branchId", required = false) Integer branchId,
                                    @RequestParam(value = "userIds", required = false) List<Integer> userIds,
                                    RedirectAttributes redirectAttributes) {

        try {
            List<SalaryCalculationDto> calculations = salaryService.calculateMonthlySalary(month, year, branchId);

            if (userIds != null && !userIds.isEmpty()) {
                calculations = calculations.stream()
                        .filter(calc -> calc.getUser() != null &&
                                calc.getUser().getUserId() != null &&
                                userIds.contains(calc.getUser().getUserId()))
                        .toList();
            }

            List<SalaryCalculationDto> newRecords = calculations.stream()
                    .filter(calc -> !calc.isHasSalaryRecord())
                    .toList();

            if (!newRecords.isEmpty()) {
                salaryService.saveBatchSalaryRecords(newRecords, branchId);
                redirectAttributes.addFlashAttribute("success",
                        "Đã lưu " + newRecords.size() + " bản ghi lương thành công!");
            } else {
                redirectAttributes.addFlashAttribute("warning",
                        "Không có bản ghi lương mới nào để lưu. Tất cả đã được tính toán trước đó.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Có lỗi xảy ra khi lưu bản ghi lương: " + e.getMessage());
        }

        return "redirect:/salary/calculate?month=" + month + "&year=" + year +
                (branchId != null ? "&branchId=" + branchId : "");
    }

    @GetMapping("/records")
    public String showSalaryRecords(Model model,
                                    @RequestParam(value = "month", required = false) Integer month,
                                    @RequestParam(value = "year", required = false) Integer year,
                                    @RequestParam(value = "branchId", required = false) Integer branchId) {

        // Set default values if not provided
        LocalDate now = LocalDate.now();
        if (month == null) month = now.getMonthValue();
        if (year == null) year = now.getYear();

        // Load data
        List<Branch> branches = branchRepository.findByIsActiveTrue();
        List<SalaryRecord> salaryRecords = salaryService.getSalaryRecords(month, year, branchId);

        model.addAttribute("salaryRecords", salaryRecords);
        model.addAttribute("branches", branches);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedBranchId", branchId);
        model.addAttribute("monthName", getMonthName(month));

        return "salary/records";
    }

    @GetMapping("/detail/{userId}")
    public String showSalaryDetail(@PathVariable Integer userId,
                                   @RequestParam("month") Integer month,
                                   @RequestParam("year") Integer year,
                                   Model model) {

        // Calculate salary for specific user
        SalaryCalculationDto calculation = salaryService.calculateUserSalary(
                userRepository.findById(userId).orElse(null), month, year);

        model.addAttribute("calculation", calculation);
        model.addAttribute("monthName", getMonthName(month));

        return "salary/detail";
    }

    // Helper method to get month name in Vietnamese
    private String getMonthName(Integer month) {
        String[] monthNames = {
                "", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
        };
        return monthNames[month];
    }

    @Autowired
    private com.spazone.repository.UserRepository userRepository;
}