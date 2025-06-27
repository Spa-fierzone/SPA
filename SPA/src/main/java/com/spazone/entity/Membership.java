package com.spazone.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer membershipId;

    private String name;

    @Column(length = 4000)
    private String description;

    private BigDecimal price;

    private Integer durationMonths; // Thời hạn theo tháng

    private Integer discountPercent; // Phần trăm giảm giá

    private Integer maxBookingsPerMonth; // Số lần đặt tối đa mỗi tháng

    private Boolean priorityBooking; // Ưu tiên đặt lịch

    private Boolean freeConsultation; // Tư vấn miễn phí

    @Column(length = 4000)
    private String benefits; // Các quyền lợi khác

    private String status = "active";

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "membership", cascade = CascadeType.ALL)
    private List<CustomerMembership> customerMemberships;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Membership() {}

    public Membership(String name, String description, BigDecimal price, Integer durationMonths,
                      Integer discountPercent, Integer maxBookingsPerMonth, Boolean priorityBooking,
                      Boolean freeConsultation, String benefits) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMonths = durationMonths;
        this.discountPercent = discountPercent;
        this.maxBookingsPerMonth = maxBookingsPerMonth;
        this.priorityBooking = priorityBooking;
        this.freeConsultation = freeConsultation;
        this.benefits = benefits;
    }

    // Getters and Setters
    public Integer getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Integer membershipId) {
        this.membershipId = membershipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Integer getMaxBookingsPerMonth() {
        return maxBookingsPerMonth;
    }

    public void setMaxBookingsPerMonth(Integer maxBookingsPerMonth) {
        this.maxBookingsPerMonth = maxBookingsPerMonth;
    }

    public Boolean getPriorityBooking() {
        return priorityBooking;
    }

    public void setPriorityBooking(Boolean priorityBooking) {
        this.priorityBooking = priorityBooking;
    }

    public Boolean getFreeConsultation() {
        return freeConsultation;
    }

    public void setFreeConsultation(Boolean freeConsultation) {
        this.freeConsultation = freeConsultation;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<CustomerMembership> getCustomerMemberships() {
        return customerMemberships;
    }

    public void setCustomerMemberships(List<CustomerMembership> customerMemberships) {
        this.customerMemberships = customerMemberships;
    }
}