package com.blooddonation.dto.request;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.UrgencyLevel;

import lombok.Data;
import lombok.Builder;

import jakarta.validation.constraints.*;

@Data
@Builder
public class BloodRequestDto {

    @NotNull
    private BloodGroup bloodGroup;

    @Min(1)
    @Max(10)
    private Integer unitsRequired;

    @NotBlank
    private String hospitalName;

    private String location;
    private Double latitude;
    private Double longitude;

    @NotNull
    private UrgencyLevel urgencyLevel;

    private Long targetDonorId;
    private Long targetHospitalId;

}