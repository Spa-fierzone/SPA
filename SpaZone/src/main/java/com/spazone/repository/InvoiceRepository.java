package com.spazone.repository;

import com.spazone.entity.Appointment;
import com.spazone.entity.Invoice;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    Invoice findByAppointment(Appointment appointment);

    List<Invoice> findByCustomer(User customer);
}
