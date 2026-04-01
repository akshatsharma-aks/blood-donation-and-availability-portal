package com.blooddonation.dto.request;



import com.blooddonation.enums.BloodGroup;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;



@Data

public class DonorProfileDto {



    @NotBlank(message = "Name is required")

    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")

    private String name;
    private String city;
    private String state;



    @NotNull(message = "Blood group is required")

    private BloodGroup bloodGroup;



    @NotBlank(message = "Phone number is required")

    @Pattern(regexp = "^[+]?[0-9]{10,15}$",

            message = "Invalid phone number format")

    private String phone;



    @DecimalMin(value = "-90.0",  message = "Invalid latitude")

    @DecimalMax(value = "90.0",   message = "Invalid latitude")

    private Double latitude;



    @DecimalMin(value = "-180.0", message = "Invalid longitude")

    @DecimalMax(value = "180.0",  message = "Invalid longitude")

    private Double longitude;



    /** Date of the donor's most recent blood donation. */

    private LocalDate lastDonationDate;



    /**

     * Whether the donor is currently available to donate.

     * Defaults to true on profile creation.

     */

    private boolean availableToDonate = true;

    private LocalDate availableFromDate;
    private LocalDate availableToDate;

    @Enumerated(EnumType.STRING)
    private com.blooddonation.enums.Willingness willingness;
}