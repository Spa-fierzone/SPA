package com.spazone.service.impl;

import com.spazone.entity.TreatmentRecord;
import com.spazone.repository.TreatmentRecordRepository;
import com.spazone.service.TreatmentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentRecordServiceImpl implements TreatmentRecordService {

    @Autowired
    private TreatmentRecordRepository treatmentRecordRepository;

    @Override
    public TreatmentRecord save(TreatmentRecord treatmentRecord) {
        return treatmentRecordRepository.save(treatmentRecord);
    }

    @Override
    public List<TreatmentRecord> findByAppointmentId(Integer appointmentId) {
        return treatmentRecordRepository.findByAppointmentAppointmentId(appointmentId);
    }

    @Override
    public List<TreatmentRecord> findByTechnicianId(Integer technicianId) {
        return treatmentRecordRepository.findByTechnicianUserId(technicianId);
    }

    @Override
    public TreatmentRecord findById(Integer id) {
        return treatmentRecordRepository.findById(id).orElse(null);
    }
}
