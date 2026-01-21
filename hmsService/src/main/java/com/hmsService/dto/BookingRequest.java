package com.hmsService.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Integer roomNo;
    @NotNull
    private LocalDate checkIn;
    @NotNull
    @Future
    private LocalDate checkOut;
}
