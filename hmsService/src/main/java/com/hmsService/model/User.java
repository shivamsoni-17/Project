package com.hmsService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String username;
    private String password;
    @Default
    private String role = "customer";
    @Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
