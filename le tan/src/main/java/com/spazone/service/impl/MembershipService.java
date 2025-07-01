package com.spazone.service.impl;

import com.spazone.dto.MembershipDTO;
import com.spazone.dto.PayOSItem;
import com.spazone.dto.PayOSPaymentRequest;
import com.spazone.dto.PayOSPaymentResponse;
import com.spazone.entity.CustomerMembership;
import com.spazone.entity.Membership;
import com.spazone.entity.MembershipOrder;
import com.spazone.entity.User;
import com.spazone.repository.CustomerMembershipRepository;
import com.spazone.repository.MembershipOrderRepository;
import com.spazone.repository.MembershipRepository;
import com.spazone.service.impl.PayOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipOrderRepository orderRepository;

    @Autowired
    private CustomerMembershipRepository customerMembershipRepository;

    @Autowired
    private PayOSService payOSService;

    @Value("${app.base-url}")
    private String baseUrl;

    public List<MembershipDTO> getAllActiveMemberships() {
        List<Membership> memberships = membershipRepository.findActiveMembershipsOrderByPrice();
        return memberships.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MembershipDTO> getMembershipsByDuration(Integer durationMonths) {
        List<Membership> memberships = membershipRepository.findActiveMembershipsByDuration(durationMonths);
        return memberships.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Membership> getMembershipById(Integer membershipId) {
        return membershipRepository.findById(membershipId);
    }

    public MembershipOrder createMembershipOrder(User customer, Integer membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        // Calculate prices
        BigDecimal originalPrice = membership.getPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal finalPrice = originalPrice;

        if (membership.getDiscountPercent() != null && membership.getDiscountPercent() > 0) {
            discountAmount = originalPrice.multiply(BigDecimal.valueOf(membership.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100));
            finalPrice = originalPrice.subtract(discountAmount);
        }

        // Generate unique order code
        Long orderCode = generateOrderCode();

        MembershipOrder order = new MembershipOrder(orderCode, customer, membership,
                originalPrice, finalPrice, discountAmount);

        return orderRepository.save(order);
    }

    public PayOSPaymentResponse createPaymentLink(MembershipOrder order) {
        try {
            PayOSPaymentRequest request = new PayOSPaymentRequest();
            request.setOrderCode(order.getOrderCode());
            request.setAmount(order.getFinalPrice().multiply(BigDecimal.valueOf(100)).intValue()); // PayOS expects amount in cents
            // Shorten description to max 25 characters
            String membershipName = order.getMembership().getName();
            String desc = ("Thanh toán: " + membershipName);
            if (desc.length() > 25) desc = desc.substring(0, 25);
            request.setDescription(desc);
            request.setReturnUrl(baseUrl + "/membership/payment/success");
            request.setCancelUrl(baseUrl + "/membership/payment/cancel");

            // Create item list
            List<PayOSItem> items = Arrays.asList(
                    new PayOSItem(order.getMembership().getName(), 1,
                            order.getFinalPrice().multiply(BigDecimal.valueOf(100)).intValue())
            );
            request.setItems(items);

            return payOSService.createPaymentLink(request);

        } catch (Exception e) {
            throw new RuntimeException("Error creating payment link: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void processSuccessfulPayment(Long orderCode, String transactionId) {
        MembershipOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == MembershipOrder.OrderStatus.PAID) {
            return; // Already processed
        }

        // Update order status
        order.setStatus(MembershipOrder.OrderStatus.PAID);
        order.setPaymentTransactionId(transactionId);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // Create customer membership
        createCustomerMembership(order);
    }

    private void createCustomerMembership(MembershipOrder order) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(order.getMembership().getDurationMonths());

        CustomerMembership customerMembership = new CustomerMembership(
                order.getCustomer(),
                order.getMembership(),
                startDate,
                endDate
        );

        customerMembershipRepository.save(customerMembership);
    }

    public void cancelOrder(Long orderCode, String reason) {
        MembershipOrder order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(MembershipOrder.OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Cancel payment in PayOS
        payOSService.cancelPayment(orderCode, reason);
    }

    public Optional<CustomerMembership> getActiveCustomerMembership(User customer) {
        return customerMembershipRepository.findActiveCustomerMembership(customer, LocalDateTime.now());
    }

    public List<CustomerMembership> getCustomerMembershipHistory(User customer) {
        return customerMembershipRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    public List<MembershipOrder> getCustomerOrderHistory(User customer) {
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    // Scheduled tasks
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void expirePendingOrders() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(15);
        List<MembershipOrder> expiredOrders = orderRepository.findExpiredPendingOrders(expiredBefore);

        for (MembershipOrder order : expiredOrders) {
            order.setStatus(MembershipOrder.OrderStatus.EXPIRED);
            orderRepository.save(order);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *") // Run on 1st day of every month at midnight
    public void resetMonthlyBookingCounts() {
        LocalDateTime now = LocalDateTime.now();
        List<CustomerMembership> memberships = customerMembershipRepository.findMembershipsToResetBookingCount(now);

        for (CustomerMembership membership : memberships) {
            membership.resetMonthlyBookingCount();
            customerMembershipRepository.save(membership);
        }
    }

    @Scheduled(cron = "0 0 1 * * *") // Run daily at 1 AM
    public void expireCustomerMemberships() {
        LocalDateTime now = LocalDateTime.now();
        List<CustomerMembership> expiredMemberships = customerMembershipRepository.findExpiredMemberships(now);

        for (CustomerMembership membership : expiredMemberships) {
            membership.setStatus("expired");
            customerMembershipRepository.save(membership);
        }
    }

    // Helper methods
    private Long generateOrderCode() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private MembershipDTO convertToDTO(Membership membership) {
        MembershipDTO dto = new MembershipDTO();
        dto.setMembershipId(membership.getMembershipId());
        dto.setName(membership.getName());
        dto.setDescription(membership.getDescription());
        dto.setPrice(membership.getPrice());
        dto.setDurationMonths(membership.getDurationMonths());
        dto.setDiscountPercent(membership.getDiscountPercent());
        dto.setMaxBookingsPerMonth(membership.getMaxBookingsPerMonth());
        dto.setPriorityBooking(membership.getPriorityBooking());
        dto.setFreeConsultation(membership.getFreeConsultation());
        dto.setBenefits(membership.getBenefits());
        dto.setStatus(membership.getStatus());

        // Parse benefits string to list
        if (membership.getBenefits() != null && !membership.getBenefits().trim().isEmpty()) {
            dto.setBenefitsList(Arrays.asList(membership.getBenefits().split("\\n")));
        }

        return dto;
    }

    public MembershipOrder findOrderByOrderCode(Long orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElse(null);
    }

    public void activateMembership(MembershipOrder order, PayOSPaymentResponse paymentInfo) {
        if (order.getStatus() == MembershipOrder.OrderStatus.PAID) return;
        order.setStatus(MembershipOrder.OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
        createCustomerMembership(order);
    }

    public void failMembershipOrder(MembershipOrder order) {
        order.setStatus(MembershipOrder.OrderStatus.PENDING);
        orderRepository.save(order);
    }

    public void cancelMembershipOrder(MembershipOrder order) {
        order.setStatus(MembershipOrder.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public PayOSPaymentResponse getPaymentInfo(Long orderCode) {
        return payOSService.getPaymentInfo(orderCode);
    }
}