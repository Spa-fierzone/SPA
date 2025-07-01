package com.spazone.controller;

import com.spazone.entity.Appointment;
import com.spazone.entity.AppointmentHandled;
import com.spazone.entity.User;
import com.spazone.repository.AppointmentHandledRepository;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.BranchRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.AppointmentService;
import com.spazone.service.InvoiceService;
import com.spazone.service.SpaServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/reception")
public class ReceptionController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentHandledRepository appointmentHandledRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    private BranchRepository branchRepository;

    @GetMapping("/appointments")
    public String viewTodayAppointments(Model model) {
        model.addAttribute("appointments", appointmentService.getTodayAppointments());
        return "reception/appointments";
    }

    @PostMapping("/appointments/{id}/checkin")
    public String checkIn(@PathVariable Integer id, Authentication authentication) {
        Appointment appointment = appointmentService.checkIn(id);
        String username = authentication.getName();
        User receptionist = userRepository.findByUsername(username).orElse(null);

        if (appointment != null && receptionist != null) {
            AppointmentHandled handled = new AppointmentHandled();
            handled.setAppointment(appointment);
            handled.setReceptionist(receptionist);
            handled.setHandledAt(LocalDateTime.now());
            appointmentHandledRepository.save(handled);
        }

        return "redirect:/reception/appointments";
    }

    @PostMapping("/appointments/{id}/checkout")
    public String checkOut(@PathVariable Integer id) {
        appointmentService.checkOut(id);
        return "redirect:/reception/appointments";
    }

    @GetMapping("/invoices/pending")
    public String viewPendingAppointments(Model model) {
        List<Appointment> pendingAppointments = appointmentRepository.findCompletedAppointmentsWithUnpaidInvoice();
        model.addAttribute("appointments", pendingAppointments);
        return "reception/pending-invoices";
    }

    @PostMapping("/invoices/create")
    public String createInvoice(@RequestParam("appointmentId") Integer appointmentId,
                                @RequestParam("notes") String notes,
                                RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentService.findById(appointmentId);
        invoiceService.createInvoiceForAppointment(appointment, "CASH", notes);
        redirectAttributes.addFlashAttribute("success", "Đã tạo hóa đơn và gửi email.");
        return "redirect:/reception/invoices/pending";
    }

    @GetMapping("/appointments/create")
    public String showCreateAppointmentForm(Model model) {
        model.addAttribute("services", spaServiceService.findAllActive());
        model.addAttribute("customers", userRepository.findAllCustomers());
        model.addAttribute("technicians", userRepository.findAllTechnicians());
        model.addAttribute("branches", branchRepository.findAll());
        // Initially, no technicians are marked as available/busy
        return "reception/appointment-create";
    }

    @PostMapping("/appointments/create")
    public String createAppointment(@RequestParam Integer customerId,
                                    @RequestParam Integer serviceId,
                                    @RequestParam String appointmentDateTime,
                                    @RequestParam Integer technicianId,
                                    @RequestParam Integer branchId,
                                    RedirectAttributes redirectAttributes) {
        boolean success = appointmentService.createAppointment(customerId, serviceId, appointmentDateTime, technicianId, branchId);
        if (success) {
            Appointment appointment = appointmentService.findLatestAppointment(customerId, serviceId, technicianId, branchId);
            invoiceService.createInvoiceForAppointment(appointment, "CASH", "Invoice created automatically after appointment.");
            redirectAttributes.addFlashAttribute("success", "Appointment and invoice created successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create appointment.");
        }
        return "redirect:/reception/appointments";
    }
}
