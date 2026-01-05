package com.streetburger.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
    private String phoneNumber;
    private String guestName;
    private Integer guestCount;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private String specialRequests;
}
