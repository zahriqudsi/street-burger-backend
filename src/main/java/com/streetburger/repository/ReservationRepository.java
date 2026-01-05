package com.streetburger.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.streetburger.model.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByPhoneNumber(String phoneNumber);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByReservationDate(LocalDate date);

    List<Reservation> findByPhoneNumberOrderByReservationDateDescReservationTimeDesc(String phoneNumber);

    List<Reservation> findByStatus(Reservation.ReservationStatus status);
}
