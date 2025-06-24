package com.spazone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Integer feedbackId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "appointment_id", nullable = false)
    private Integer appointmentId;

    @Column(name = "service_rating", nullable = false)
    private Integer serviceRating;

    @Column(name = "technician_rating", nullable = false)
    private Integer technicianRating;

    @Column(name = "facility_rating", nullable = false)
    private Integer facilityRating;

    @Column(name = "overall_rating", nullable = false)
    private Integer overallRating;

    @Column(length = 4000)
    private String comments;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Many-to-One relationship with User (Customer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private User customer;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Feedback() {
    }

    public Feedback(Integer customerId, Integer appointmentId, Integer serviceRating,
                    Integer technicianRating, Integer facilityRating, Integer overallRating,
                    String comments, Boolean isAnonymous) {
        this.customerId = customerId;
        this.appointmentId = appointmentId;
        this.serviceRating = serviceRating;
        this.technicianRating = technicianRating;
        this.facilityRating = facilityRating;
        this.overallRating = overallRating;
        this.comments = comments;
        this.isAnonymous = isAnonymous;
    }

    // Getters and Setters
    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getServiceRating() {
        return serviceRating;
    }

    public void setServiceRating(Integer serviceRating) {
        this.serviceRating = serviceRating;
    }

    public Integer getTechnicianRating() {
        return technicianRating;
    }

    public void setTechnicianRating(Integer technicianRating) {
        this.technicianRating = technicianRating;
    }

    public Integer getFacilityRating() {
        return facilityRating;
    }

    public void setFacilityRating(Integer facilityRating) {
        this.facilityRating = facilityRating;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    // Utility method to get customer name (handles anonymous feedback)
    public String getCustomerDisplayName() {
        if (isAnonymous != null && isAnonymous) {
            return "Anonymous";
        }
        return customer != null ? customer.getFullName() : "Unknown Customer";
    }

    // Utility method to calculate average rating
    public Double getAverageRating() {
        return (serviceRating + technicianRating + facilityRating + overallRating) / 4.0;
    }
}