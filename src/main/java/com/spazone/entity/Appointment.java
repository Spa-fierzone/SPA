package com.spazone.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appointmentId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    private LocalDateTime appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(name = "checkin_time")
    private LocalDateTime checkinTime;

    @Column(name = "checkout_time")
    private LocalDateTime checkoutTime;

    private String status;
    @Column(length = 4000)
    private String notes;
    @Column(length = 4000)
    private String cancellationReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Boolean reminderSent;
    private LocalDateTime reminderSentAt;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    @ManyToOne
    @JoinColumn(name = "preferred_technician_id")
    private User preferredTechnician;

    @Column(length = 4000)
    private String specialRequests;

    private String reminderMethod;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    private List<TreatmentRecord> treatmentRecords;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Appointment() {
    }

    public Appointment(Integer appointmentId, User customer, Branch branch, Service service, User technician, LocalDateTime appointmentDate, LocalDateTime startTime, LocalDateTime endTime, String status, String notes, String cancellationReason, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean reminderSent, LocalDateTime reminderSentAt, LocalDateTime checkInTime, LocalDateTime checkOutTime, User preferredTechnician, String specialRequests, String reminderMethod) {
        this.appointmentId = appointmentId;
        this.customer = customer;
        this.branch = branch;
        this.service = service;
        this.technician = technician;
        this.appointmentDate = appointmentDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.notes = notes;
        this.cancellationReason = cancellationReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reminderSent = reminderSent;
        this.reminderSentAt = reminderSentAt;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.preferredTechnician = preferredTechnician;
        this.specialRequests = specialRequests;
        this.reminderMethod = reminderMethod;
    }

    public LocalDateTime getCheckinTime() {
        return checkinTime;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public List<TreatmentRecord> getTreatmentRecords() {
        return treatmentRecords;
    }

    public void setCheckinTime(LocalDateTime checkinTime) {
        this.checkinTime = checkinTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public void setTreatmentRecords(List<TreatmentRecord> treatmentRecords) {
        this.treatmentRecords = treatmentRecords;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public User getCustomer() {
        return customer;
    }

    public Branch getBranch() {
        return branch;
    }

    public Service getService() {
        return service;
    }

    public User getTechnician() {
        return technician;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getReminderSent() {
        return reminderSent;
    }

    public LocalDateTime getReminderSentAt() {
        return reminderSentAt;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public User getPreferredTechnician() {
        return preferredTechnician;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public String getReminderMethod() {
        return reminderMethod;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setTechnician(User technician) {
        this.technician = technician;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setReminderSent(Boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public void setReminderSentAt(LocalDateTime reminderSentAt) {
        this.reminderSentAt = reminderSentAt;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setPreferredTechnician(User preferredTechnician) {
        this.preferredTechnician = preferredTechnician;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public void setReminderMethod(String reminderMethod) {
        this.reminderMethod = reminderMethod;
    }
}

