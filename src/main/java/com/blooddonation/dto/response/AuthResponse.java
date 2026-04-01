package com.blooddonation.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String tokenType = "Bearer";

    private String email;

    private String role;

    private Long userId;
}