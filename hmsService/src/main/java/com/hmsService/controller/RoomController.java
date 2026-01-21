package com.hmsService.controller;

import com.hmsService.model.Room;
import com.hmsService.service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:4200")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> available() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Room>> search(@RequestParam("checkIn") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                                             @RequestParam(value = "type", required = false) String type) {
        Room.Type roomType = null;
        if (type != null && !type.isBlank()) {
            try {
                roomType = Room.Type.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid room type");
            }
        }
        return ResponseEntity.ok(roomService.search(checkIn, roomType));
    }

    @GetMapping
    public ResponseEntity<List<Room>> listAll() {
        return ResponseEntity.ok(roomService.listAll());
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.save(room));
    }

    @PutMapping("/{roomNo}/status")
    public ResponseEntity<Room> updateStatus(@PathVariable Integer roomNo, @RequestBody Map<String, String> request) {
        Room.Status status = Room.Status.valueOf(request.get("status").toUpperCase());
        return ResponseEntity.ok(roomService.updateStatus(roomNo, status));
    }
}
