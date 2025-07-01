package com.spazone.repository;

import com.spazone.entity.TreatmentRecord;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TreatmentRecordRepository extends JpaRepository<TreatmentRecord, Integer> {

    @Query("SELECT t FROM TreatmentRecord t WHERE t.appointment.appointmentId = :appointmentId")
    List<TreatmentRecord> findByAppointmentAppointmentId(@Param("appointmentId") Integer appointmentId);

    @Query("SELECT t FROM TreatmentRecord t WHERE t.technician.userId = :technicianId")
    List<TreatmentRecord> findByTechnicianUserId(@Param("technicianId") Integer technicianId);

    List<TreatmentRecord> findByTechnician(User technician);

    TreatmentRecord findByAppointment_AppointmentId(Integer appointmentAppointmentId);

}
