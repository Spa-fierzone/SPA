package com.spazone.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_handled")
public class AppointmentHandled {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Appointment appointment;

    @ManyToOne
    private User receptionist;

    private LocalDateTime handledAt;

    public AppointmentHandled() {
    }

    public AppointmentHandled(Long id, Appointment appointment, User receptionist, LocalDateTime handledAt) {
        this.id = id;
        this.appointment = appointment;
        this.receptionist = receptionist;
        this.handledAt = handledAt;
    }

    public Long getId() {
        return id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public User getReceptionist() {
        return receptionist;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setReceptionist(User receptionist) {
        this.receptionist = receptionist;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        this.handledAt = handledAt;
    }
}
