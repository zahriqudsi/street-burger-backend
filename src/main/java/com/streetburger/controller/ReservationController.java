package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.ReservationRequest;
import com.streetburger.model.Reservation;
import com.streetburger.repository.ReservationRepository;
import com.streetburger.repository.UserRepository;

@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservations", description = "Table reservation management")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ApiResponse<Reservation>> addReservation(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ReservationRequest request) {

        Reservation reservation = new Reservation();
        reservation.setUser(currentUser);
        reservation.setPhoneNumber(
                request.getPhoneNumber() != null ? request.getPhoneNumber() : currentUser.getPhoneNumber());
        reservation.setGuestName(request.getGuestName() != null ? request.getGuestName() : currentUser.getName());
        reservation.setGuestCount(request.getGuestCount());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reservation created", saved));
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update a reservation")
    public ResponseEntity<ApiResponse<Reservation>> updateReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequest request) {

        return reservationRepository.findById(id)
                .map(existing -> {
                    if (request.getGuestCount() != null)
                        existing.setGuestCount(request.getGuestCount());
                    if (request.getReservationDate() != null)
                        existing.setReservationDate(request.getReservationDate());
                    if (request.getReservationTime() != null)
                        existing.setReservationTime(request.getReservationTime());
                    if (request.getSpecialRequests() != null)
                        existing.setSpecialRequests(request.getSpecialRequests());
                    if (request.getGuestName() != null)
                        existing.setGuestName(request.getGuestName());
                    existing.setUpdatedAt(LocalDateTime.now());

                    reservationRepository.save(existing);
                    return ResponseEntity.ok(ApiResponse.success("Reservation updated", existing));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Reservation not found")));
    }

    @GetMapping("/getByPhone/{phoneNumber}")
    @Operation(summary = "Get reservations by phone number")
    public ResponseEntity<ApiResponse<List<Reservation>>> getByPhone(@PathVariable String phoneNumber) {
        List<Reservation> reservations = reservationRepository
                .findByPhoneNumberOrderByReservationDateDescReservationTimeDesc(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @GetMapping("/getById/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ApiResponse<Reservation>> getById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> ResponseEntity.ok(ApiResponse.success(reservation)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Reservation not found")));
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get all reservations (Admin)")
    public ResponseEntity<ApiResponse<List<Reservation>>> getAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(reservations));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Cancel/delete a reservation")
    public ResponseEntity<ApiResponse<String>> deleteReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
                    reservationRepository.save(reservation);
                    return ResponseEntity.ok(ApiResponse.<String>success("Reservation cancelled", null));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Reservation not found")));
    }

    @PutMapping("/confirm/{id}")
    @Operation(summary = "Confirm a reservation (Admin)")
    public ResponseEntity<ApiResponse<Reservation>> confirmReservation(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    reservationRepository.save(reservation);
                    return ResponseEntity.ok(ApiResponse.success("Reservation confirmed", reservation));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Reservation not found")));
    }
}
