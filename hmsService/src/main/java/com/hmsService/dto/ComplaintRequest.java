package com.hmsService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComplaintRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String contact;
    private Integer roomNo;
    @NotBlank
    private String category;
    @NotBlank
    private String description;
}
