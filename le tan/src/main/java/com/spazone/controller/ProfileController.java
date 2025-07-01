package com.spazone.controller;

import com.spazone.dto.UpdateProfileDto;
import com.spazone.dto.UserProfileDto;
import com.spazone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.spazone.entity.Membership;
import com.spazone.entity.MembershipOrder;
import com.spazone.entity.CustomerMembership;
import com.spazone.entity.FavoriteService;
import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceRating;
import com.spazone.repository.MembershipOrderRepository;
import com.spazone.repository.CustomerMembershipRepository;
import com.spazone.repository.FavoriteServiceRepository;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.ServiceRatingRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final MembershipOrderRepository membershipOrderRepository;
    private final CustomerMembershipRepository customerMembershipRepository;
    private final FavoriteServiceRepository favoriteServiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final ServiceRatingRepository serviceRatingRepository;

    public ProfileController(UserService userService,
                            MembershipOrderRepository membershipOrderRepository,
                            CustomerMembershipRepository customerMembershipRepository,
                            FavoriteServiceRepository favoriteServiceRepository,
                            AppointmentRepository appointmentRepository,
                            ServiceRatingRepository serviceRatingRepository) {
        this.userService = userService;
        this.membershipOrderRepository = membershipOrderRepository;
        this.customerMembershipRepository = customerMembershipRepository;
        this.favoriteServiceRepository = favoriteServiceRepository;
        this.appointmentRepository = appointmentRepository;
        this.serviceRatingRepository = serviceRatingRepository;
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
            // Membership status
            Optional<CustomerMembership> membershipOpt = customerMembershipRepository.findByCustomerEmail(username);
            membershipOpt.ifPresent(membership -> model.addAttribute("membership", membership));
            // Treatment plans (appointments)
            List<Appointment> appointments = appointmentRepository.findByCustomerEmail(username);
            model.addAttribute("appointments", appointments);
            // Favorite services
            List<FavoriteService> favorites = favoriteServiceRepository.findByCustomerEmail(username);
            model.addAttribute("favoriteServices", favorites);
            // Service ratings
            List<ServiceRating> ratings = serviceRatingRepository.findByCustomerEmail(username);
            model.addAttribute("serviceRatings", ratings);
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
