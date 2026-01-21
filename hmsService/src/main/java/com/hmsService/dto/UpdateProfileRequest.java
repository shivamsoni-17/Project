package com.hmsService.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String mobile;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private String role;
}

