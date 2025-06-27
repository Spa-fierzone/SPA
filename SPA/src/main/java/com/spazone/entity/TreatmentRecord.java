package com.spazone.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_records")
public class TreatmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Column(length = 4000)
    private String preTreatmentNotes;

    @Column(length = 4000)
    private String postTreatmentNotes;

    private String beforeImageUrl;
    private String afterImageUrl;

    private LocalDateTime treatmentDate;

    @Column(length = 4000)
    private String customerFeedback;

    @Column(length = 4000)
    private String followUpNotes;

    private Integer treatmentProgress;

    private LocalDate nextAppointmentRecommendation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public TreatmentRecord() {
    }

    public Integer getRecordId() {
        return recordId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public User getTechnician() {
        return technician;
    }

    public String getPreTreatmentNotes() {
        return preTreatmentNotes;
    }

    public String getPostTreatmentNotes() {
        return postTreatmentNotes;
    }

    public String getBeforeImageUrl() {
        return beforeImageUrl;
    }

    public String getAfterImageUrl() {
        return afterImageUrl;
    }

    public LocalDateTime getTreatmentDate() {
        return treatmentDate;
    }

    public String getCustomerFeedback() {
        return customerFeedback;
    }

    public String getFollowUpNotes() {
        return followUpNotes;
    }

    public Integer getTreatmentProgress() {
        return treatmentProgress;
    }

    public LocalDate getNextAppointmentRecommendation() {
        return nextAppointmentRecommendation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public void setPreTreatmentNotes(String preTreatmentNotes) {
        this.preTreatmentNotes = preTreatmentNotes;
    }

    public void setPostTreatmentNotes(String postTreatmentNotes) {
        this.postTreatmentNotes = postTreatmentNotes;
    }

    public void setBeforeImageUrl(String beforeImageUrl) {
        this.beforeImageUrl = beforeImageUrl;
    }

    public void setAfterImageUrl(String afterImageUrl) {
        this.afterImageUrl = afterImageUrl;
    }

    public void setTreatmentDate(LocalDateTime treatmentDate) {
        this.treatmentDate = treatmentDate;
    }

    public void setCustomerFeedback(String customerFeedback) {
        this.customerFeedback = customerFeedback;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }

    public void setTreatmentProgress(Integer treatmentProgress) {
        this.treatmentProgress = treatmentProgress;
    }

    public void setNextAppointmentRecommendation(LocalDate nextAppointmentRecommendation) {
        this.nextAppointmentRecommendation = nextAppointmentRecommendation;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
