package com.spazone.service.impl;

import com.spazone.entity.Appointment;
import com.spazone.entity.Branch;
import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import com.spazone.repository.InvoiceRepository;
import com.spazone.repository.ServiceRepository;
import com.spazone.service.EmailService;
import com.spazone.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public Invoice generateInvoiceForAppointment(Appointment appointment) {
        Integer serviceId = appointment.getService().getServiceId();
        com.spazone.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Dịch vụ không tồn tại"));

        appointment.setService(service);

        BigDecimal price = service.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal tax = price.multiply(new BigDecimal("0.10"));
        BigDecimal finalAmount = price.add(tax).subtract(discount);

        Invoice invoice = new Invoice();
        invoice.setAppointment(appointment);
        invoice.setCustomer(appointment.getCustomer());
        invoice.setBranch(appointment.getBranch());
        invoice.setTotalAmount(price);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setFinalAmount(finalAmount);
        invoice.setPaymentStatus("unpaid");
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());

        return invoiceRepository.save(invoice);
    }


    @Override
    public Invoice getById(Integer id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> getByCustomer(User customer) {
        return invoiceRepository.findByCustomer(customer);
    }

    @Override
    public Invoice createInvoiceForAppointment(Appointment appointment, String paymentMethod, String notes) {
        Invoice invoice = new Invoice();
        invoice.setAppointment(appointment);
        invoice.setCustomer(appointment.getCustomer());
        invoice.setBranch(appointment.getBranch());
        invoice.setTotalAmount(appointment.getService().getPrice());
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTaxAmount(appointment.getService().getPrice().multiply(new BigDecimal("0.1")));
        invoice.setFinalAmount(invoice.getTotalAmount().add(invoice.getTaxAmount()));
        invoice.setPaymentStatus("PAID");
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentDate(LocalDateTime.now());
        invoice.setPaymentNotes(notes);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());

        invoiceRepository.save(invoice);

        // Gửi email hóa đơn
        emailService.sendInvoiceEmail(invoice);

        return invoice;
    }

    @Override
    public Invoice createInvoiceForAppointments(List<Appointment> appointments, String paymentMethod, String notes) {
        if (appointments == null || appointments.isEmpty()) {
            throw new IllegalArgumentException("Danh sách lịch hẹn trống");
        }
        // Assume all appointments are for the same customer and branch
        User customer = appointments.get(0).getCustomer();
        Branch branch = appointments.get(0).getBranch();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal tax = BigDecimal.ZERO;
        for (Appointment appt : appointments) {
            BigDecimal price = appt.getService().getPrice();
            totalAmount = totalAmount.add(price);
            tax = tax.add(price.multiply(new BigDecimal("0.10")));
        }
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount.add(tax).subtract(discount);
        Invoice invoice = new Invoice();
        // Do NOT set appointments on invoice
        invoice.setCustomer(customer);
        invoice.setBranch(branch);
        invoice.setTotalAmount(totalAmount);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setFinalAmount(finalAmount);
        invoice.setPaymentStatus("unpaid");
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentNotes(notes);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setCreatedAt(LocalDateTime.now());
        Invoice saved = invoiceRepository.save(invoice);
        // Optionally, send invoice email
        emailService.sendInvoiceEmail(saved);
        return saved;
    }
}
