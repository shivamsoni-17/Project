package com.hmsService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayBillRequest {
    @NotNull
    private Long billId;
    @NotBlank
    private String mode; // UPI/CARD/CASH/NETBANKING/WALLET
    @NotNull
    private BigDecimal amount;
}

