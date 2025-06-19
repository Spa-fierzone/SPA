package com.spazone.service;

import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;

import java.util.List;

public interface ServiceScheduleService {

    List<ServiceSchedule> getSchedulesForTechnician(User technician);


}
