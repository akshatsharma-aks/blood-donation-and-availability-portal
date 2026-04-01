package com.blooddonation.dto.request;

import com.blooddonation.enums.Role;
import com.blooddonation.enums.BloodGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // Required for Jackson JSON parsing!
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
//    @Enumerated(EnumType.STRING)
    private Role role;

    // Common Location Fields (Missing previously)
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;

    // Used for DONOR role
    private String name;
    private BloodGroup bloodGroup;
    private String phone;
//    private Double latitude;
//    private Double longitude;

    // Used for HOSPITAL role
    private String hospitalName;
    private String licenseNumber;
    private String address;
}