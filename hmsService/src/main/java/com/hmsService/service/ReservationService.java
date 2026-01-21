package com.hmsService.service;

import com.hmsService.dto.BookingRequest;
import com.hmsService.model.Reservation;
import com.hmsService.model.Room;
import com.hmsService.repository.ReservationRepository;
import com.hmsService.repository.RoomRepository;
import com.hmsService.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BillingService billingService;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository,
                              BillingService billingService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.billingService = billingService;
    }

    @Transactional
    public Reservation book(BookingRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(request.getRoomNo())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!Room.Status.VACANT.equals(room.getStatus())) {
            throw new IllegalStateException("Room not available");
        }
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException("Checkout must be after checkin");
        }

        boolean hasFutureBooking = reservationRepository.existsForRoomAfter(
                room.getRoomNo(), Reservation.Status.BOOKED, request.getCheckIn());

        if (hasFutureBooking) {
            throw new IllegalStateException("Room already booked for the selected dates");
        }

        Reservation reservation = new Reservation();
        reservation.setUserId(request.getUserId());
        reservation.setRoomNo(room.getRoomNo());
        reservation.setCheckInDate(request.getCheckIn());
        reservation.setCheckOutDate(request.getCheckOut());
        reservation.setStatus(Reservation.Status.BOOKED);
        reservation.setUpcoming(request.getCheckIn());
        room.setStatus(Room.Status.OCCUPIED);
        room.setAvailabilityDate(request.getCheckOut());

        roomRepository.save(room);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Automatically create a bill for this reservation
        long numberOfNights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (numberOfNights <= 0) {
            numberOfNights = 1; // Minimum 1 night
        }
        billingService.createBillForReservation(
                savedReservation.getId(),
                room.getPrice(),
                numberOfNights
        );

        return savedReservation;
    }

    public List<Reservation> getByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return reservationRepository.findByUser(userId);
    }

    @Transactional
    public Reservation cancel(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setStatus(Reservation.Status.CANCELLED);
        Room room = roomRepository.findById(reservation.getRoomNo())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(Room.Status.VACANT);
        roomRepository.save(room);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation update(Long reservationId, BookingRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // ownership/basic validation
        if (!reservation.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("User mismatch");
        }
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException("Checkout must be after checkin");
        }

        // Only allow updating dates for active booking
        if (Reservation.Status.CANCELLED.equals(reservation.getStatus())) {
            throw new IllegalStateException("Cancelled reservation cannot be updated");
        }

        // ensure room exists
        Room room = roomRepository.findById(reservation.getRoomNo())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        boolean hasConflict = reservationRepository.existsForRoomAfter(
                room.getRoomNo(), Reservation.Status.BOOKED, request.getCheckIn());
        if (hasConflict && !reservation.getCheckInDate().equals(request.getCheckIn())) {
            throw new IllegalStateException("Room already booked for the selected dates");
        }

        reservation.setCheckInDate(request.getCheckIn());
        reservation.setCheckOutDate(request.getCheckOut());
        reservation.setUpcoming(request.getCheckIn());

        // keep room availability date aligned
        room.setAvailabilityDate(request.getCheckOut());
        roomRepository.save(room);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> search(Long userId, Integer roomNo, String from, String to) {
        List<Reservation> all = reservationRepository.findAll();
        return all.stream()
                .filter(r -> userId == null || r.getUserId().equals(userId))
                .filter(r -> roomNo == null || r.getRoomNo().equals(roomNo))
                .filter(r -> from == null || from.isBlank() || !r.getCheckInDate().isBefore(LocalDate.parse(from)))
                .filter(r -> to == null || to.isBlank() || !r.getCheckOutDate().isAfter(LocalDate.parse(to)))
                .collect(Collectors.toList());
    }
}
