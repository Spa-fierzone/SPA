package com.spazone.controller;

import com.spazone.entity.Appointment;
import com.spazone.service.AppointmentService;
import com.spazone.service.CloudinaryService;
import com.spazone.service.TreatmentRecordService;
import com.spazone.service.UserService;
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
                                @RequestParam("afterImageFile") MultipartFile afterImage) {
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
        return "redirect:/technician/schedule";
    }

}
