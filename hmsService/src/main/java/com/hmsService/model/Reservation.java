package com.hmsService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    public enum Status { BOOKED, CANCELLED }

    private Long id;
    private Long userId;
    private Integer roomNo;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    @Default
    private Status status = Status.BOOKED;
    private LocalDate upcoming;
}
