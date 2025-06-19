package com.spazone.service.impl;

import com.spazone.entity.TreatmentRecord;
import com.spazone.entity.User;
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
    public TreatmentRecord save(TreatmentRecord record) {
        return treatmentRecordRepository.save(record);
    }

    @Override
    public TreatmentRecord findById(Integer id) {
        return treatmentRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy treatment record ID: " + id));
    }

    @Override
    public List<TreatmentRecord> findByTechnician(User technician) {
        return treatmentRecordRepository.findByTechnician(technician);
    }

    @Override
    public TreatmentRecord findByAppointmentId(Integer appointmentId) {
        return treatmentRecordRepository.findByAppointment_AppointmentId(appointmentId);
    }
}
