package com.spazone.service;

import com.spazone.entity.Appointment;
import com.spazone.entity.Invoice;
import com.spazone.entity.User;

import java.util.List;

public interface InvoiceService {
    Invoice generateInvoiceForAppointment(Appointment appointment);
    Invoice getById(Integer id);
    void save(Invoice invoice);
    List<Invoice> getByCustomer(User customer);

}

