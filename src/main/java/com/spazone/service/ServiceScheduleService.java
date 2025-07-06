package com.spazone.service;

import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ServiceScheduleService {

    List<ServiceSchedule> getSchedulesForTechnician(User technician);

    Page<ServiceSchedule> searchSchedulesForTechnician(Integer technicianId, String serviceName, Integer branchId, Boolean active, Pageable pageable);

    ServiceSchedule save(ServiceSchedule schedule);
}
