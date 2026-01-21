package com.hmsService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    public enum PayStatus { UNPAID, PAID, PARTIALLY_PAID, REFUNDED, CANCELLED }

    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    @Default
    private BigDecimal addCharges = BigDecimal.valueOf(8.00);
    @Default
    private PayStatus payStatus = PayStatus.UNPAID;
    private Long transactionId;
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
