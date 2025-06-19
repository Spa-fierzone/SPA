package com.spazone.service.impl;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentRepository;
import com.spazone.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public void create(Appointment appointment) {
        LocalTime startTime = appointment.getStartTime();

        if (appointment.getService() == null || appointment.getService().getDuration() < 0) {
            throw new IllegalArgumentException("Dịch vụ hoặc thời lượng dịch vụ không hợp lệ.");
        }

        int duration = appointment.getService().getDuration();
        LocalTime endTime = startTime.plusMinutes(duration);

        appointment.setEndTime(endTime);

        if (appointment.getStatus() == null) {
            appointment.setStatus("scheduled");
        }

        appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getByCustomer(User customer) {
        return appointmentRepository.findByCustomer(customer);
    }

    // ✅ Thêm chức năng huỷ lịch hẹn
    @Override
    public void cancelAppointment(Integer appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Lịch hẹn không tồn tại."));

        if (!"scheduled".equalsIgnoreCase(appointment.getStatus()) &&
                !"booked".equalsIgnoreCase(appointment.getStatus())) {
            throw new IllegalStateException("Chỉ có thể huỷ lịch hẹn ở trạng thái scheduled/booked.");
        }

        appointment.setStatus("cancelled");
        appointment.setCancellationReason(reason);
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
    }
}
