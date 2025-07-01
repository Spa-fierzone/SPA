// MembershipController.java
package com.spazone.controller;

import com.spazone.dto.MembershipDTO;
import com.spazone.dto.PayOSPaymentResponse;
import com.spazone.entity.CustomerMembership;
import com.spazone.entity.Membership;
import com.spazone.entity.MembershipOrder;
import com.spazone.entity.User;
import com.spazone.service.impl.MembershipService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserService userService;

    // Show all available memberships or filter by duration
    @GetMapping
    public String showMemberships(Model model,
                                  @RequestParam(required = false) Integer duration) {
        List<MembershipDTO> memberships;

        if (duration != null) {
            memberships = membershipService.getMembershipsByDuration(duration);
        } else {
            memberships = membershipService.getAllActiveMemberships();
        }

        model.addAttribute("memberships", memberships);
        return "membership/list";  // Display memberships in the 'membership/list.html' view
    }

    // Show membership details
    @GetMapping("/{membershipId}")
    public String showMembershipDetails(@PathVariable Integer membershipId, Model model) {
        Optional<Membership> membershipOpt = membershipService.getMembershipById(membershipId);

        if (membershipOpt.isPresent()) {
            Membership membership = membershipOpt.get();
            model.addAttribute("membership", membership);
            return "membership/detail";  // Show membership details in the 'membership/detail.html' view
        } else {
            return "redirect:/membership";  // Redirect to the membership list if not found
        }
    }

    // Register a customer for a membership
    @PostMapping("/register/{membershipId}")
    public String registerForMembership(@PathVariable Integer membershipId,
                                        Authentication authentication,
                                        RedirectAttributes redirectAttributes) {
        String customerName = authentication.getName();
        User customer = userService.findByEmail(customerName);
        try {
            // Create membership order and payment link
            MembershipOrder order = membershipService.createMembershipOrder(customer, membershipId);
            PayOSPaymentResponse paymentResponse = membershipService.createPaymentLink(order);

            if (paymentResponse == null || paymentResponse.getError() != 0 || paymentResponse.getData() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", paymentResponse != null ? paymentResponse.getMessage() : "Không thể tạo link thanh toán");
                return "redirect:/membership";
            }

            // Save order with status 'pending' (should be handled in createMembershipOrder)
            // Redirect to PayOS checkout
            return "redirect:" + paymentResponse.getData().getCheckoutUrl();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/membership";
        }
    }

    // Handle PayOS return (success/fail)
    @GetMapping("/payos-return")
    public String handlePayOSReturn(@RequestParam("orderCode") Long orderCode,
                                    @RequestParam(value = "status", required = false) String status,
                                    Model model) {
        try {
            PayOSPaymentResponse paymentInfo = membershipService.getPaymentInfo(orderCode);
            MembershipOrder order = membershipService.findOrderByOrderCode(orderCode);
            if (order != null) {
                if (paymentInfo != null && paymentInfo.getData() != null && "PAID".equals(paymentInfo.getData().getStatus())) {
                    membershipService.activateMembership(order, paymentInfo);
                    model.addAttribute("success", true);
                    model.addAttribute("message", "Thanh toán thành công! Gói thành viên đã được kích hoạt.");
                } else {
                    membershipService.failMembershipOrder(order);
                    model.addAttribute("success", false);
                    model.addAttribute("message", "Thanh toán thất bại hoặc bị hủy.");
                }
                model.addAttribute("order", order);
            }
            return "payment/result";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xử lý kết quả thanh toán: " + e.getMessage());
            return "payment/error";
        }
    }

    // Handle PayOS cancel
    @GetMapping("/payos-cancel")
    public String handlePayOSCancel(@RequestParam("orderCode") Long orderCode, Model model) {
        MembershipOrder order = membershipService.findOrderByOrderCode(orderCode);
        if (order != null) {
            membershipService.cancelMembershipOrder(order);
            model.addAttribute("message", "Giao dịch đã được hủy!");
            model.addAttribute("order", order);
        }
        return "payment/result";
    }

    // View customer's active membership
    @GetMapping("/my-membership")
    public String viewMyMembership(Authentication authentication, Model model) {
        User customer = (User) authentication.getPrincipal();
        Optional<CustomerMembership> customerMembershipOpt = membershipService.getActiveCustomerMembership(customer);

        if (customerMembershipOpt.isPresent()) {
            model.addAttribute("customerMembership", customerMembershipOpt.get());
            return "membership/my-membership";  // Show the active membership in the 'membership/myMembership.html' view
        } else {
            model.addAttribute("errorMessage", "You don't have an active membership.");
            return "membership/my-membership";
        }
    }

    // View customer's membership order history
    @GetMapping("/order-history")
    public String viewOrderHistory(Authentication authentication, Model model) {
        User customer = (User) authentication.getPrincipal();
        List<MembershipOrder> orderHistory = membershipService.getCustomerOrderHistory(customer);

        model.addAttribute("orderHistory", orderHistory);
        return "membership/order-history";  // Show the order history in the 'membership/orderHistory.html' view
    }

    // Handle membership cancellation by customer
    @PostMapping("/cancel/{orderCode}")
    public String cancelMembershipOrder(@PathVariable Long orderCode, RedirectAttributes redirectAttributes) {
        try {
            membershipService.cancelOrder(orderCode, "User canceled the order.");
            redirectAttributes.addFlashAttribute("successMessage", "Your membership order has been canceled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel the membership order.");
        }
        return "redirect:/membership/order-history";  // Redirect to the order history after cancellation
    }
}
