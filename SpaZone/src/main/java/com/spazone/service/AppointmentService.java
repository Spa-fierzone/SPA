package com.spazone.service;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;

import java.util.List;

public interface AppointmentService {
    void create(Appointment appointment);
    List<Appointment> getByCustomer(User customer);
    void cancelAppointment(Integer appointmentId, String reason); // <-- Thêm dòng này
}
