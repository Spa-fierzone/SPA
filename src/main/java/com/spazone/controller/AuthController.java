package com.spazone.controller;

import com.spazone.dto.ChangePasswordDto;
import com.spazone.dto.RegisterDto;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out.");
        }
        return "auth/login";
    }


    // Trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registerDto") RegisterDto registerDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.registerUser(registerDto);
            redirectAttributes.addFlashAttribute("email", registerDto.getEmail());
            return "redirect:/auth/verify-otp";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String showOtpForm(@ModelAttribute("email") String email, Model model) {
        model.addAttribute("email", email); // giữ email để xác thực
        return "auth/verify_otp";
    }

    // Xử lý xác thực OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("email") String email,
                            @RequestParam("otp") String otp,
                            Model model) {
        try {
            userService.verifyEmail(email, otp);
            model.addAttribute("message", "Xác thực thành công! Bạn có thể đăng nhập.");
            return "auth/verify_success";
        } catch (Exception e) {
            model.addAttribute("email", email);
            model.addAttribute("error", e.getMessage());
            return "auth/verify_otp";
        }
    }

    // Trang yêu cầu đặt lại mật khẩu
    @GetMapping("/password-reset/request")
    public String showPasswordResetRequestForm() {
        return "auth/password_reset_request";
    }

    // Xử lý gửi email đặt lại mật khẩu
    @PostMapping("/password-reset/request")
    public String initiatePasswordReset(@RequestParam("email") String email, Model model) {
        try {
            userService.initiatePasswordReset(email);
            model.addAttribute("message", "If your email is registered, a reset link has been sent.");
            return "auth/password_reset_request";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/password_reset_request";
        }
    }

    // Trang đặt lại mật khẩu
    @GetMapping("/password-reset")
    public String showPasswordResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/password_reset";
    }

    // Xử lý đặt lại mật khẩu
    @PostMapping("/password-reset")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                Model model) {
        try {
            userService.resetPassword(token, newPassword);
            model.addAttribute("message", "Password reset successful. You can now login.");
            return "auth/password_reset_success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("token", token);
            return "auth/password_reset";
        }
    }

    // Trang thay đổi mật khẩu (cần login)
    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "auth/change_password";
    }

    // Xử lý thay đổi mật khẩu (cần login)
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto,
                                 Authentication authentication,
                                 Model model) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        try {
            userService.changePassword(username, dto);
            model.addAttribute("message", "Password changed successfully.");
            return "auth/change_password_success";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/change_password";
        }
    }

}
