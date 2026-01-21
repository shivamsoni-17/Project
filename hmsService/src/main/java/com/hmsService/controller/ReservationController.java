package com.hmsService.controller;

import com.hmsService.dto.BookingRequest;
import com.hmsService.model.Reservation;
import com.hmsService.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> book(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(reservationService.book(request));
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer roomNo,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(reservationService.search(userId, roomNo, from, to));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.getByUser(userId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancel(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> update(@PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(reservationService.update(id, request));
    }
}
