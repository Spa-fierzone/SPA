package com.spazone.service.impl;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentRepository;
import com.spazone.service.AppointmentService;
import com.spazone.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private NotificationService notificationService;

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

        // ✅ Gửi thông báo đặt lịch
        notificationService.notify(
                appointment.getCustomer(),
                "Đặt lịch thành công",
                "Bạn đã đặt lịch dịch vụ '" + appointment.getService().getName() +
                        "' vào ngày " + appointment.getAppointmentDate() +
                        " lúc " + appointment.getStartTime(),
                "appointment"
        );
    }

    @Override
    public List<Appointment> getByCustomer(User customer) {
        return appointmentRepository.findByCustomer(customer);
    }

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

        // ✅ Gửi thông báo huỷ lịch
        notificationService.notify(
                appointment.getCustomer(),
                "Huỷ lịch hẹn",
                "Lịch hẹn dịch vụ '" + appointment.getService().getName() +
                        "' vào ngày " + appointment.getAppointmentDate() +
                        " đã bị huỷ. Lý do: " + reason,
                "cancel"
        );
    }
}
