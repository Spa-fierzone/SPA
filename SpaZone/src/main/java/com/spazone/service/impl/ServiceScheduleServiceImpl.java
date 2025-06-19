package com.spazone.service.impl;

import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import com.spazone.repository.ServiceScheduleRepository;
import com.spazone.service.ServiceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceScheduleServiceImpl implements ServiceScheduleService {

    @Autowired
    private ServiceScheduleRepository serviceScheduleRepository;

    @Override
    public List<ServiceSchedule> getSchedulesForTechnician(User technician) {
        return serviceScheduleRepository.findByTechnician(technician);
    }
}
