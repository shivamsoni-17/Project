package com.hmsService.controller;

import com.hmsService.model.Room;
import com.hmsService.repository.BillRepository;
import com.hmsService.repository.ComplaintRepository;
import com.hmsService.repository.ReservationRepository;
import com.hmsService.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final ComplaintRepository complaintRepository;
    private final BillRepository billRepository;

    public AdminController(RoomRepository roomRepository,
                          ReservationRepository reservationRepository,
                          ComplaintRepository complaintRepository,
                          BillRepository billRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.complaintRepository = complaintRepository;
        this.billRepository = billRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalRooms = roomRepository.count();
            long vacantRooms = roomRepository.findByStatus(Room.Status.VACANT).size();
            long occupiedRooms = roomRepository.findByStatus(Room.Status.OCCUPIED).size();
            long totalReservations = reservationRepository.findAll().size();
            long openComplaints = complaintRepository.findAll().stream()
                    .filter(c -> c.getStatus() == com.hmsService.model.Complaint.Status.OPEN)
                    .count();
            long unpaidBills = billRepository.findAll().stream()
                    .filter(b -> b.getPayStatus() == com.hmsService.model.Bill.PayStatus.UNPAID)
                    .count();
            
            stats.put("totalRooms", totalRooms);
            stats.put("vacantRooms", vacantRooms);
            stats.put("occupiedRooms", occupiedRooms);
            stats.put("totalReservations", totalReservations);
            stats.put("openComplaints", openComplaints);
            stats.put("unpaidBills", unpaidBills);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Return default values on error
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRooms", 0L);
            stats.put("vacantRooms", 0L);
            stats.put("occupiedRooms", 0L);
            stats.put("totalReservations", 0L);
            stats.put("openComplaints", 0L);
            stats.put("unpaidBills", 0L);
            return ResponseEntity.ok(stats);
        }
    }
}
