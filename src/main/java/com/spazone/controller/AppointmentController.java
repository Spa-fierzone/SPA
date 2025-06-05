package com.spazone.controller;

import com.spazone.entity.Appointment;
import com.spazone.entity.Invoice;
import com.spazone.entity.Service;
import com.spazone.entity.User;
import com.spazone.repository.UserRepository;
import com.spazone.service.AppointmentService;
import com.spazone.service.BranchService;
import com.spazone.service.InvoiceService;
import com.spazone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvoiceService invoiceService;

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null &&
                authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }


    @GetMapping("/book/{serviceId}")
    public String showBookingForm(@PathVariable Integer serviceId, Model model) {
        Appointment appointment = new Appointment();
        Service service = new Service();
        service.setServiceId(serviceId);
        appointment.setService(service);

        model.addAttribute("appointment", appointment);
        model.addAttribute("branches", branchService.findAllActive());
        model.addAttribute("technicians", userRepository.findUsersByRole("TECHNICIAN"));
        return "appointment/form";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute Appointment appointment,
                                Authentication authentication ,
                                RedirectAttributes redirectAttributes) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        User customer = userService.findByEmail(username);
        appointment.setCustomer(customer);
        appointmentService.create(appointment);

        // ✅ Tạo hóa đơn sau khi đặt lịch
        Invoice invoice = invoiceService.generateInvoiceForAppointment(appointment);

        return "redirect:/invoices/" + invoice.getInvoiceId();
    }

    @GetMapping("/my")
    public String viewMyAppointments(Model model,
                                     Authentication authentication) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        String username = authentication.getName();
        User customer = userService.findByEmail(username);
        List<Appointment> appointments = appointmentService.getByCustomer(customer);
        model.addAttribute("appointments", appointments);
        return "appointment/list";
    }
}

