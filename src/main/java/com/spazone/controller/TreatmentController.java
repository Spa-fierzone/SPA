package com.spazone.controller;

import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceSchedule;
import com.spazone.service.*;
import com.spazone.entity.User;
import com.spazone.entity.TreatmentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/technician/treatments")
public class TreatmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private TreatmentRecordService treatmentService;

    @Autowired
    private ServiceScheduleService serviceScheduleService;

    @GetMapping("/view/{id}")
    public String viewTreatment(@PathVariable Integer id, Model model) {
        TreatmentRecord record = treatmentService.findById(id);
        model.addAttribute("treatment", record);
        return "technician/treatment-view";
    }

    @GetMapping("/new/{appointmentId}")
    public String newTreatmentForm(@PathVariable Integer appointmentId, Model model, Principal principal) {
        Appointment appointment = appointmentService.findById(appointmentId);
        User technician = userService.findByUsername(principal.getName());

        TreatmentRecord treatment = new TreatmentRecord();
        treatment.setAppointment(appointment);
        treatment.setTechnician(technician);
        treatment.setTreatmentDate(LocalDateTime.now());

        model.addAttribute("treatment", treatment);
        return "technician/treatment-form";
    }

    @PostMapping("/save")
    public String saveTreatment(@ModelAttribute TreatmentRecord treatment,
                                @RequestParam("beforeImageFile") MultipartFile beforeImage,
                                @RequestParam("afterImageFile") MultipartFile afterImage,
                                @RequestParam(value = "nextAppointmentDateTime", required = false) String nextAppointmentDateTimeStr,
                                @RequestParam(value = "createNextAppointment", required = false) String createNextAppointment) {
        if (!beforeImage.isEmpty()) {
            String beforeUrl = cloudinaryService.uploadImage(beforeImage); // ví dụ dùng Cloudinary
            treatment.setBeforeImageUrl(beforeUrl);
        }
        if (!afterImage.isEmpty()) {
            String afterUrl = cloudinaryService.uploadImage(afterImage);
            treatment.setAfterImageUrl(afterUrl);
        }

        treatment.setCreatedAt(LocalDateTime.now());
        treatmentService.save(treatment);

        // --- Create new Appointment and ServiceSchedule after treatment record ---
        if ("on".equals(createNextAppointment)) {
            Appointment prevAppointment = treatment.getAppointment();
            if (prevAppointment != null) {
                LocalDateTime nextAppointmentDateTime = null;
                if (nextAppointmentDateTimeStr != null && !nextAppointmentDateTimeStr.isEmpty()) {
                    nextAppointmentDateTime = LocalDateTime.parse(nextAppointmentDateTimeStr);
                } else {
                    nextAppointmentDateTime = LocalDateTime.now().plusDays(7);
                }
                Appointment newAppointment = new Appointment();
                newAppointment.setCustomer(prevAppointment.getCustomer());
                newAppointment.setTechnician(prevAppointment.getTechnician());
                newAppointment.setService(prevAppointment.getService());
                newAppointment.setBranch(prevAppointment.getBranch());
                newAppointment.setStartTime(nextAppointmentDateTime);
                newAppointment.setAppointmentDate(nextAppointmentDateTime);
                newAppointment.setNotes("Tái khám sau điều trị");
                appointmentService.create(newAppointment);

                ServiceSchedule newSchedule = new ServiceSchedule();
                newSchedule.setService(newAppointment.getService());
                newSchedule.setBranch(newAppointment.getBranch());
                newSchedule.setTechnician(newAppointment.getTechnician());
                newSchedule.setStartTime(newAppointment.getStartTime());
                newSchedule.setEndTime(newAppointment.getStartTime().plusMinutes(newAppointment.getService().getDuration()));
                newSchedule.setDayOfWeek(newAppointment.getStartTime().getDayOfWeek().getValue());
                newSchedule.setActive(true);
                serviceScheduleService.save(newSchedule);
            }
        }
        // --- End create new Appointment and ServiceSchedule ---

        return "redirect:/technician/schedule";
    }

}
