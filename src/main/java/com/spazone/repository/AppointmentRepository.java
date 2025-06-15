package com.spazone.repository;

import com.spazone.entity.Appointment;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByCustomer(User customer);

}
