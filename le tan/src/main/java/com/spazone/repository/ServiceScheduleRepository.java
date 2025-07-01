package com.spazone.repository;

import com.spazone.entity.Branch;
import com.spazone.entity.Service;
import com.spazone.entity.ServiceSchedule;
import com.spazone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT s FROM ServiceSchedule s WHERE (:technicianId IS NULL OR s.technician.userId = :technicianId) " +
            "AND (:serviceName IS NULL OR LOWER(s.service.name) LIKE LOWER(CONCAT('%', :serviceName, '%'))) " +
            "AND (:branchId IS NULL OR s.branch.branchId = :branchId) " +
            "AND (:active IS NULL OR s.isActive = :active)")
    Page<ServiceSchedule> searchSchedules(
            @Param("technicianId") Integer technicianId,
            @Param("serviceName") String serviceName,
            @Param("branchId") Integer branchId,
            @Param("active") Boolean active,
            Pageable pageable);

}
