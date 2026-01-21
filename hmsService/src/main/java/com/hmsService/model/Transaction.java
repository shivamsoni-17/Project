package com.hmsService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    public enum Status { SUCCESS, FAILED, PENDING, REFUNDED }

    public enum Mode { UPI, CARD, CASH, NETBANKING, WALLET }

    private Long id;
    @Default
    private LocalDate payDate = LocalDate.now();
    private Status status;
    private Mode mode;
    private BigDecimal amount;
}
