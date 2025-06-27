package com.spazone.controller;

import com.spazone.entity.*;
import com.spazone.repository.AppointmentRepository;
import com.spazone.repository.UserRepository;
import com.spazone.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private SpaServiceService spaServiceService;

    @Autowired
    private AppointmentRepository appointmentRepository;

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

    @GetMapping("/book")
    public String showBookingFormMulti(@RequestParam(value = "serviceIds", required = false) List<Integer> serviceIds, Model model) {
        model.addAttribute("branches", branchService.findAllActive());
        model.addAttribute("technicians", userRepository.findUsersByRole("TECHNICIAN"));
        model.addAttribute("services", spaServiceService.findAllActive());
        model.addAttribute("selectedServiceIds", serviceIds);
        return "appointment/form";
    }

    @PostMapping("/book")
    public String submitBooking(@RequestParam("serviceIds") List<Integer> serviceIds,
                                @RequestParam("branchId") Integer branchId,
                                @RequestParam("technicianId") Integer technicianId,
                                @RequestParam("datePart") String datePart,
                                @RequestParam("timePart") String timePart,
                                @RequestParam(value = "notes", required = false) String notes,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        try {
            LocalDateTime startTime = LocalDateTime.parse(datePart + "T" + timePart);
            LocalDateTime maxAllowed = LocalDateTime.now().plusMonths(3);
            if (startTime.isAfter(maxAllowed)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn chỉ có thể đặt lịch trong vòng 3 tháng tới.");
                return "redirect:/appointments/book";
            }
            Branch branch = branchService.getById(branchId);
            User customer = userService.findByEmail(authentication.getName());
            for (Integer serviceId : serviceIds) {
                Service service = spaServiceService.getServiceById(serviceId);
                int duration = service.getDuration();
                LocalDateTime endTime = startTime.plusMinutes(duration);
                User technician;
                if (technicianId == null || technicianId == 0) {
                    List<User> availableTechnicians = userService.findTechniciansByBranch(branchId);
                    List<User> freeTechnicians = availableTechnicians.stream()
                            .filter(t -> appointmentRepository.findConflictingAppointments(t.getUserId(), startTime, endTime).isEmpty())
                            .collect(Collectors.toList());
                    if (freeTechnicians.isEmpty()) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Không có kỹ thuật viên nào rảnh tại thời gian đã chọn cho dịch vụ: " + service.getName());
                        return "redirect:/appointments/book";
                    }
                    Collections.shuffle(freeTechnicians);
                    technician = freeTechnicians.get(0);
                } else {
                    technician = userService.getUserById(technicianId);
                }
                Appointment appointment = new Appointment();
                appointment.setService(service);
                appointment.setBranch(branch);
                appointment.setTechnician(technician);
                appointment.setCustomer(customer);
                appointment.setStartTime(startTime);
                appointment.setAppointmentDate(startTime);
                appointment.setNotes(notes);
                appointmentService.create(appointment);
                // Optionally, generate invoice per appointment or after all
            }
            // Redirect to my appointments or invoice page
            return "redirect:/appointments/my";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể đặt lịch: " + e.getMessage());
            return "redirect:/appointments/book";
        }
    }

    @PostMapping("/book-multi")
    public String submitBookingMulti(@RequestParam("serviceIds") List<Integer> serviceIds,
                                     @RequestParam("branchId") Integer branchId,
                                     @RequestParam("technicianId") Integer technicianId,
                                     @RequestParam("datePart") String datePart,
                                     @RequestParam("timePart") String timePart,
                                     @RequestParam(value = "notes", required = false) String notes,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        if (!isAuthenticatedUser(authentication)) {
            return "redirect:/auth/login";
        }
        try {
            LocalDateTime startTime = LocalDateTime.parse(datePart + "T" + timePart);
            LocalDateTime maxAllowed = LocalDateTime.now().plusMonths(3);
            if (startTime.isAfter(maxAllowed)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn chỉ có thể đặt lịch trong vòng 3 tháng tới.");
                return "redirect:/appointments/book";
            }
            Branch branch = branchService.getById(branchId);
            User customer = userService.findByEmail(authentication.getName());
            List<Appointment> createdAppointments = new java.util.ArrayList<>();
            for (Integer serviceId : serviceIds) {
                Service service = spaServiceService.getServiceById(serviceId);
                int duration = service.getDuration();
                LocalDateTime endTime = startTime.plusMinutes(duration);
                User technician;
                if (technicianId == null || technicianId == 0) {
                    List<User> availableTechnicians = userService.findTechniciansByBranch(branchId);
                    List<User> freeTechnicians = availableTechnicians.stream()
                            .filter(t -> appointmentRepository.findConflictingAppointments(t.getUserId(), startTime, endTime).isEmpty())
                            .collect(Collectors.toList());
                    if (freeTechnicians.isEmpty()) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Không có kỹ thuật viên nào rảnh tại thời gian đã chọn cho dịch vụ: " + service.getName());
                        return "redirect:/appointments/book";
                    }
                    Collections.shuffle(freeTechnicians);
                    technician = freeTechnicians.get(0);
                } else {
                    technician = userService.getUserById(technicianId);
                }
                Appointment appointment = new Appointment();
                appointment.setService(service);
                appointment.setBranch(branch);
                appointment.setTechnician(technician);
                appointment.setCustomer(customer);
                appointment.setStartTime(startTime);
                appointment.setAppointmentDate(startTime);
                appointment.setNotes(notes);
                appointmentService.create(appointment);
                createdAppointments.add(appointment);
            }
            // Generate a single invoice for all appointments
            Invoice invoice = invoiceService.createInvoiceForAppointments(createdAppointments, "PAYOS", notes);
            return "redirect:/payment/payos/" + invoice.getInvoiceId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể đặt lịch: " + e.getMessage());
            return "redirect:/appointments/book";
        }
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

    @GetMapping("/{id}/treatment-records")
    public String viewTreatmentRecords(@PathVariable("id") Integer appointmentId, Model model) {
        Appointment appointment = appointmentService.findByIdWithTreatmentRecords(appointmentId);
        if (appointment == null) {
            return "redirect:/appointments?error=AppointmentNotFound";
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("treatmentRecords", appointment.getTreatmentRecords());
        return "appointment/treatment-records";
    }

    @GetMapping("/book-multi")
    public String showMultiBookingForm(Model model) {
        model.addAttribute("branches", branchService.findAllActive());
        model.addAttribute("technicians", userRepository.findUsersByRole("TECHNICIAN"));
        model.addAttribute("services", spaServiceService.findAllActive());
        return "appointment/form-multi";
    }
}
