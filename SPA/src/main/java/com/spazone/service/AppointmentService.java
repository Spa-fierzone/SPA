package com.spazone.service;

import com.spazone.dto.TimeSlot;
import com.spazone.entity.Appointment;
import com.spazone.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    void create(Appointment appointment);

    List<Appointment> getByCustomer(User customer);

    List<TimeSlot> suggestFreeSlots(Integer technicianId, LocalDateTime date, int duration);

    List<Appointment> getTodayAppointments();

    Appointment findById(Integer id);

    Appointment checkIn(Integer appointmentId);

    void checkOut(Integer appointmentId);

    Appointment findByIdWithTreatmentRecords(Integer id);


}
