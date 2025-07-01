package com.spazone.service;

import com.spazone.entity.Room;
import com.spazone.entity.Branch;
import com.spazone.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> findAvailableRooms(Branch branch, LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.findAvailableRooms(branch, startTime, endTime);
    }

    public List<Room> findByBranch(Branch branch) {
        return roomRepository.findByBranch(branch);
    }

    public Room findById(Integer id) {
        return roomRepository.findById(id).orElse(null);
    }
}

