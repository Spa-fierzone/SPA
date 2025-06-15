package com.spazone.controller.manager;

import com.spazone.entity.User;
import com.spazone.repository.RoleRepository;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/manager")
public class ListStaffController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/staff-list")
    public String showStaffList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        int size = 10;

        // Chỉ lấy nhân viên có role RECEPTIONIST hoặc TECHNICIAN
        Page<User> staffPage = userService.getUsersWithFilters(keyword, status, role, page, size);

        model.addAttribute("staff", staffPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", staffPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("role", role);

        // Chỉ lấy role RECEPTIONIST và TECHNICIAN
//        model.addAttribute("staffRoles", roleRepository.findByRoleName(Set.of("RECEPTIONIST", "TECHNICIAN").toString()));

        return "manager/staff-management";
    }

    @PostMapping("/staff/{id}/status")
    public String changeStaffStatus(@PathVariable Integer id,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        userService.changeUserStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái nhân viên.");
        return "redirect:/manager/staff-list";
    }

    @PostMapping("/staff/{id}/roles")
    public String updateStaffRoles(@PathVariable Integer id,
                                   @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds,
                                   RedirectAttributes redirectAttributes) {
        // Kiểm tra role chỉ được phép là RECEPTIONIST hoặc TECHNICIAN
        if (roleIds != null && !roleIds.isEmpty()) {
            boolean validRoles = roleRepository.findAllById(roleIds).stream()
                    .allMatch(role -> role.getRoleName().equals("RECEPTIONIST") ||
                            role.getRoleName().equals("TECHNICIAN"));

            if (!validRoles) {
                redirectAttributes.addFlashAttribute("error", "Chỉ được phép phân quyền Lễ tân hoặc Kỹ thuật viên.");
                return "redirect:/manager/staff-list";
            }
        }

        userService.updateUserRoles(id, roleIds != null ? roleIds : Set.of());
        redirectAttributes.addFlashAttribute("message", "Cập nhật quyền nhân viên thành công.");
        return "redirect:/manager/staff-list";
    }
}