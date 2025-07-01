package com.spazone.service.impl;

import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import com.spazone.repository.ServiceScheduleRepository;
import com.spazone.service.ServiceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<ServiceSchedule> searchSchedulesForTechnician(Integer technicianId, String serviceName, Integer branchId, Boolean active, Pageable pageable) {
        return serviceScheduleRepository.searchSchedules(technicianId, serviceName, branchId, active, pageable);
    }

    @Override
    public ServiceSchedule save(ServiceSchedule schedule) {
        return serviceScheduleRepository.save(schedule);
    }
}
