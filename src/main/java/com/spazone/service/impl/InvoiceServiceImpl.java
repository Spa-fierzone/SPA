package com.spazone.service.impl;

import com.spazone.entity.Appointment;
import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import com.spazone.repository.InvoiceRepository;
import com.spazone.repository.ServiceRepository;
import com.spazone.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

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
}

