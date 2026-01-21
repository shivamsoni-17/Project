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
public class Complaint {

    public enum Status { OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED }

    private Long id;
    private Long userId;
    private String contact;
    private Integer roomNo;
    private String category;
    private String description;
    @Default
    private Status status = Status.OPEN;
    @Default
    private LocalDate createdAt = LocalDate.now();
    private LocalDate resolvedAt;
}
