package com.spazone.service;

import com.spazone.entity.TreatmentRecord;
import com.spazone.entity.User;

import java.util.List;

public interface TreatmentRecordService {
    TreatmentRecord save(TreatmentRecord record);

    TreatmentRecord findById(Integer id);

    List<TreatmentRecord> findByTechnician(User technician);

    TreatmentRecord findByAppointmentId(Integer appointmentId);
}
