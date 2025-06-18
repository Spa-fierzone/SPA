package com.spazone.dto;

import com.spazone.entity.Appointment;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.TreatmentRecord;

public class TechnicianScheduleDto {
    private ServiceSchedule schedule;
    private Appointment appointment;
    private TreatmentRecord treatmentRecord;

    public TechnicianScheduleDto(ServiceSchedule schedule, Appointment appointment, TreatmentRecord treatmentRecord) {
        this.schedule = schedule;
        this.appointment = appointment;
        this.treatmentRecord = treatmentRecord;
    }

    public ServiceSchedule getSchedule() { return schedule; }
    public Appointment getAppointment() { return appointment; }
    public TreatmentRecord getTreatmentRecord() { return treatmentRecord; }
}
