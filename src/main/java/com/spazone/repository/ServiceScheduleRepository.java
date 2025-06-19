package com.spazone.repository;

import com.spazone.entity.Branch;
import com.spazone.entity.Service;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceScheduleRepository extends JpaRepository<ServiceSchedule, Integer> {
    List<ServiceSchedule> findByTechnician(User technician);

    boolean existsByTechnicianAndServiceAndBranch(User technician, Service service, Branch branch);

    boolean existsByTechnicianUserIdAndBranchBranchIdAndDayOfWeekAndStartTimeAndEndTimeAndServiceServiceId(
            Integer technicianId,
            Integer branchId,
            Integer dayOfWeek,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Integer serviceId
    );

}

