package com.spazone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_memberships")
public class CustomerMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerMembershipId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private String status = "active"; // active, expired, cancelled

    private Integer usedBookingsThisMonth = 0;

    private LocalDateTime purchaseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (purchaseDate == null) {
            purchaseDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public CustomerMembership() {}

    public CustomerMembership(User customer, Membership membership, LocalDateTime startDate, LocalDateTime endDate) {
        this.customer = customer;
        this.membership = membership;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Methods
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return "active".equals(status) && now.isAfter(startDate) && now.isBefore(endDate);
    }

    public boolean canBookThisMonth() {
        if (!isActive()) return false;
        Integer maxBookings = membership.getMaxBookingsPerMonth();
        return maxBookings == null || usedBookingsThisMonth < maxBookings;
    }

    public void incrementBookingCount() {
        this.usedBookingsThisMonth++;
    }

    public void resetMonthlyBookingCount() {
        this.usedBookingsThisMonth = 0;
    }

    // Getters and Setters
    public Integer getCustomerMembershipId() {
        return customerMembershipId;
    }

    public void setCustomerMembershipId(Integer customerMembershipId) {
        this.customerMembershipId = customerMembershipId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getUsedBookingsThisMonth() {
        return usedBookingsThisMonth;
    }

    public void setUsedBookingsThisMonth(Integer usedBookingsThisMonth) {
        this.usedBookingsThisMonth = usedBookingsThisMonth;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}