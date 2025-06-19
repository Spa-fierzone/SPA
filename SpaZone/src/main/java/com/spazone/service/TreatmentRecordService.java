package com.spazone.service;

import com.spazone.entity.TreatmentRecord;
import java.util.List;

public interface TreatmentRecordService {
    TreatmentRecord save(TreatmentRecord treatmentRecord);
    List<TreatmentRecord> findByAppointmentId(Integer appointmentId);
    List<TreatmentRecord> findByTechnicianId(Integer technicianId);
    TreatmentRecord findById(Integer id);
}
