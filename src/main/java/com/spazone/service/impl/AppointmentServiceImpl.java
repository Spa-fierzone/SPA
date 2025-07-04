package com.spazone.service.impl;

import com.spazone.dto.TimeSlot;
import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.ServiceScheduleRepository;
import com.spazone.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ServiceScheduleRepository serviceScheduleRepository;

    @Override
    public void create(Appointment appointment) {
        LocalDateTime startTime = appointment.getStartTime();

        if (appointment.getService() == null || appointment.getService().getDuration() <= 0) {
            throw new IllegalArgumentException("Dịch vụ hoặc thời lượng dịch vụ không hợp lệ.");
        }

        int duration = appointment.getService().getDuration();
        LocalDateTime endTime = startTime.plusMinutes(duration);
        appointment.setEndTime(endTime);

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                appointment.getTechnician().getUserId(),
                startTime,
                endTime
        );

        if (!conflicts.isEmpty()) {
            List<TimeSlot> suggestions = suggestFreeSlots(
                    appointment.getTechnician().getUserId(),
                    appointment.getAppointmentDate(),
                    duration
            );

            String message = "Kỹ thuật viên đã có lịch trong khoảng này.";
            if (!suggestions.isEmpty()) {
                message += " Gợi ý thời gian trống:\n" + suggestions.stream()
                        .map(TimeSlot::toString)
                        .collect(Collectors.joining("\n"));
            }

            throw new IllegalStateException(message);
        }

        if (appointment.getStatus() == null) {
            appointment.setStatus("scheduled");
        }

        appointmentRepository.save(appointment);

        int dayOfWeek = appointment.getAppointmentDate().getDayOfWeek().getValue() % 7;

        boolean exists = serviceScheduleRepository
                .existsByTechnicianUserIdAndBranchBranchIdAndDayOfWeekAndStartTimeAndEndTimeAndServiceServiceId(
                        appointment.getTechnician().getUserId(),
                        appointment.getBranch().getBranchId(),
                        dayOfWeek,
                        startTime,
                        endTime,
                        appointment.getService().getServiceId()
                );

        if (!exists) {
            ServiceSchedule schedule = new ServiceSchedule();
            schedule.setTechnician(appointment.getTechnician());
            schedule.setBranch(appointment.getBranch());
            schedule.setService(appointment.getService());
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setDayOfWeek(dayOfWeek);
            schedule.setActive(true);
            serviceScheduleRepository.save(schedule);
        }
    }

    @Override
    public List<TimeSlot> suggestFreeSlots(Integer technicianId, LocalDateTime dateTime, int serviceDurationMinutes) {

        LocalDateTime workStart = dateTime.withHour(8).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime workEnd = dateTime.withHour(18).withMinute(0).withSecond(0).withNano(0);

        // Truy vấn danh sách cuộc hẹn của kỹ thuật viên trong ngày
        List<Appointment> appointments = appointmentRepository
                .findAppointmentsByTechnicianAndDateRange(technicianId, workStart, workEnd);

        List<TimeSlot> freeSlots = new ArrayList<>();
        LocalDateTime current = workStart;

        for (Appointment a : appointments) {
            if (current.isBefore(a.getStartTime())) {
                Duration gap = Duration.between(current, a.getStartTime());
                if (gap.toMinutes() >= serviceDurationMinutes) {
                    freeSlots.add(new TimeSlot(current, a.getStartTime()));
                }
            }
            // Di chuyển thời điểm bắt đầu tiếp theo nếu cuộc hẹn hiện tại kết thúc muộn hơn
            current = a.getEndTime().isAfter(current) ? a.getEndTime() : current;
        }

        // Thêm khung giờ cuối nếu còn thời gian từ giờ hiện tại đến khi kết thúc làm việc
        if (current.isBefore(workEnd)) {
            Duration gap = Duration.between(current, workEnd);
            if (gap.toMinutes() >= serviceDurationMinutes) {
                freeSlots.add(new TimeSlot(current, workEnd));
            }
        }

        return freeSlots;
    }

    @Override
    public List<Appointment> getTodayAppointments() {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay(); // 00:00
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // 23:59:59.999999999

        return appointmentRepository.findByAppointmentDateRange(startOfDay, endOfDay);

    }

    @Override
    public Appointment findById(Integer id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public Appointment checkIn(Integer id) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null && Objects.equals(appointment.getStatus(), "scheduled")) {
            appointment.setStatus("CHECK_IN");
            appointmentRepository.save(appointment);
        }
        return appointment;
    }

    @Override
    public void checkOut(Integer appointmentId) {
        Appointment appt = findById(appointmentId);
        appt.setCheckoutTime(LocalDateTime.now());
        appt.setStatus("CHECKED_OUT");
        appointmentRepository.save(appt);
    }


    @Override
    public List<Appointment> getByCustomer(User customer) {
        return appointmentRepository.findByCustomer(customer);
    }
}
