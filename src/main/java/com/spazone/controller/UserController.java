package com.spazone.controller;

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
@RequestMapping("/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        int size = 10;
        Page<User> userPage = userService.getUsersWithFilters(keyword, status, role, page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("role", role);
        model.addAttribute("roles", roleRepository.findAll());

        return "admin/user-management";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        userService.changeUserStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "Đã cập nhật trạng thái người dùng.");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/roles")
    public String editUserRoles(@PathVariable Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", userService.getAllRoles());
        return "admin/user-roles";
    }

    // 4. Cập nhật role cho người dùng
    @PostMapping("/{id}/roles")
    public String updateUserRoles(@PathVariable Integer id,
                                  @RequestParam(value = "roleIds", required = false) Set<Integer> roleIds,
                                  RedirectAttributes redirectAttributes) {
        userService.updateUserRoles(id, roleIds != null ? roleIds : Set.of());
        redirectAttributes.addFlashAttribute("message", "Cập nhật quyền người dùng thành công.");
        return "redirect:/admin/users";
    }
}
