package com.blooddonation.dto.response;



import com.blooddonation.enums.BloodGroup;

import com.blooddonation.enums.VerificationStatus;

import lombok.Builder;

import lombok.Data;

import java.util.Map;



@Data

@Builder

public class HospitalProfileResponse {



    private Long id;

    private Long userId;

    private String hospitalName;

    private String licenseNumber;

    private String address;

    private String city;

    private Double latitude;

    private Double longitude;

    private String phone;

    private String contactEmail;

    private VerificationStatus verificationStatus;



    /**

     * Blood group -> units available map.

     * e.g. { "A_POS": 12, "O_NEG": 3 }

     * Only populated when inventory endpoint is queried.

     */

    private Map<BloodGroup, Integer> bloodInventory;



    /** Distance in km from the searching user. Null for global search. */

    private Double distanceKm;



    // ── Static factory ──────────────────────────────────────────



    public static HospitalProfileResponse from(

            com.blooddonation.entity.Hospital h,

            Double distanceKm) {

        return HospitalProfileResponse.builder()

                .id(h.getId())

                .userId(h.getUser().getId())

                .hospitalName(h.getHospitalName())

                .licenseNumber(h.getLicenseNumber())

                .address(h.getAddress())

                .latitude(h.getLatitude())

                .longitude(h.getLongitude())

                .verificationStatus(h.getVerificationStatus())

                .distanceKm(distanceKm)

                .build();

    }

}