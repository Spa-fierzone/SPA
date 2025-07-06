package com.spazone.repository;

import com.spazone.entity.Room;
import com.spazone.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByBranch(Branch branch);

    @Query("SELECT r FROM Room r WHERE r.branch = :branch AND r.status = 'available' AND r.id NOT IN ("
         + "SELECT a.room.id FROM Appointment a WHERE a.branch = :branch AND a.room IS NOT NULL "
         + "AND ((a.startTime < :endTime AND a.endTime > :startTime))"
         + ")")
    List<Room> findAvailableRooms(@Param("branch") Branch branch,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM Room r WHERE r.branch = :branch AND r.status = 'available' AND r.id NOT IN ("
         + "SELECT a.room.id FROM Appointment a WHERE a.branch = :branch AND a.room IS NOT NULL "
         + "AND ((a.startTime < :endTime AND a.endTime > :startTime))"
         + ") ORDER BY r.id ASC")
    Room findOneAvailableRoom(@Param("branch") Branch branch,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);
}
