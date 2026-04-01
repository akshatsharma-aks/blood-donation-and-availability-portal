package com.blooddonation.dto.request;



import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import lombok.Data;



@Data

public class HospitalProfileDto {



    @NotBlank(message = "Hospital name is required")

    @Size(max = 200)

    private String hospitalName;
    private String city;
    private String state;



    @NotBlank(message = "License number is required")

    @Size(max = 100)

    private String licenseNumber;



    @NotBlank(message = "Address is required")

    private String address;


    // ... existing fields ...

    @Enumerated(EnumType.STRING)
    private com.blooddonation.enums.InstitutionType institutionType = com.blooddonation.enums.InstitutionType.HOSPITAL;

    @NotNull(message = "Latitude is required")

    @DecimalMin(value = "-90.0",  message = "Invalid latitude")

    @DecimalMax(value = "90.0",   message = "Invalid latitude")

    private Double latitude;



    @NotNull(message = "Longitude is required")

    @DecimalMin(value = "-180.0", message = "Invalid longitude")

    @DecimalMax(value = "180.0",  message = "Invalid longitude")

    private Double longitude;



    /** Contact phone number for the hospital. */

    @Pattern(regexp = "^[+]?[0-9]{10,15}$",

            message = "Invalid phone number")

    private String phone;



    /** Public contact email address. */

    @Email(message = "Invalid email address")

    private String contactEmail;

}