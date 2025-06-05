package com.spazone.controller;

import com.spazone.dto.UpdateProfileDto;
import com.spazone.dto.UserProfileDto;
import com.spazone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    // Xem profile
    @GetMapping
    public String viewProfile(Authentication authentication, Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();

        try {
            UserProfileDto profile = userService.getUserProfile(username);
            model.addAttribute("profile", profile);
            return "profile/view";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load profile: " + e.getMessage());
            return "profile/view";
        }
    }

    // Trang form chỉnh sửa profile
    @GetMapping("/edit")
    public String showEditProfileForm(Authentication authentication, Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();

        try {
            UserProfileDto profile = userService.getUserProfile(username);
            UpdateProfileDto dto = new UpdateProfileDto();
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setDateOfBirth(profile.getDateOfBirth());
            dto.setGender(profile.getGender());
            dto.setAddress(profile.getAddress());

            model.addAttribute("updateProfileDto", dto);
            return "profile/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Unable to load profile for edit: " + e.getMessage());
            return "profile/edit";
        }
    }

    // Xử lý cập nhật profile
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute("updateProfileDto") UpdateProfileDto dto,
                                Authentication authentication,
                                Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();

        try {
            userService.updateUserProfile(username, dto);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            return "profile/edit";
        }
    }

    // Cập nhật ảnh đại diện
    @PostMapping("/picture")
    public String updateProfilePicture(@RequestParam("file") MultipartFile file,
                                       Authentication authentication,
                                       Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();

        try {
            userService.updateProfilePicture(username, file);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile picture: " + e.getMessage());
            // Load profile again in case of error
            try {
                UserProfileDto profile = userService.getUserProfile(username);
                model.addAttribute("profile", profile);
            } catch (Exception ex) {
                model.addAttribute("error", "Unable to load profile after picture update failure.");
            }
            return "profile/view";
        }
    }
}
