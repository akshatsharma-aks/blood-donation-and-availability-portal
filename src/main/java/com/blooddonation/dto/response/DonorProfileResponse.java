package com.blooddonation.dto.response;



import com.blooddonation.enums.BloodGroup;

import com.blooddonation.enums.VerificationStatus;

import lombok.Builder;

import lombok.Data;

import java.time.LocalDate;



@Data

@Builder

public class DonorProfileResponse {



    private Long id;

    private Long userId;

    private String name;

    private BloodGroup bloodGroup;

    private String phone;



    /** GPS coordinates — returned so receivers can render a map. */

    private Double latitude;

    private Double longitude;



    private LocalDate lastDonationDate;

    private boolean availableToDonate;



    private VerificationStatus verificationStatus;



    /**

     * Null when unverified; populated after hospital approval.

     * Verification expires every 90 days.

     */

    private LocalDate verificationExpiryDate;



    /** Calculated field — distance in km from the requester. */

    private Double distanceKm;



    // ── Static factory ──────────────────────────────────────────



    /**

     * Maps a DonorProfile entity to this response DTO.

     * Pass distanceKm from HaversineUtil if performing

     * a location-based search, otherwise pass null.

     */

    public static DonorProfileResponse from(

            com.blooddonation.entity.DonorProfile p,

            Double distanceKm) {

        return DonorProfileResponse.builder()

                .id(p.getId())

                .userId(p.getUser().getId())

                .name(p.getName())

                .bloodGroup(p.getBloodGroup())

                .phone(p.getPhone())

                .latitude(p.getLatitude())

                .longitude(p.getLongitude())

                .lastDonationDate(p.getLastDonationDate())

                .verificationStatus(p.getVerificationStatus())

                .verificationExpiryDate(p.getVerificationExpiryDate())

                .distanceKm(distanceKm)

                .build();

    }

}