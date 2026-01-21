package com.hmsService.service;

import com.hmsService.model.Room;
import com.hmsService.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(Room.Status.VACANT);
    }

    public List<Room> search(LocalDate checkIn, Room.Type type) {
        return roomRepository.searchAvailable(checkIn, type, Room.Status.VACANT);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> listAll() {
        return roomRepository.findAll();
    }

    public Room updateStatus(Integer roomNo, Room.Status status) {
        Room room = roomRepository.findById(roomNo)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        return roomRepository.save(room);
    }
}
