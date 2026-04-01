package com.blooddonation.controller;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.entity.DonorProfile;
import com.blooddonation.entity.Hospital;
import com.blooddonation.entity.User;

import com.blooddonation.dto.request.BloodRequestDto;
import com.blooddonation.dto.response.ApiResponse;

import com.blooddonation.enums.BloodGroup;

import com.blooddonation.service.ReceiverService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

//import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/receiver")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RECEIVER')")
@Tag(name = "Receiver APIs")
public class ReceiverController {

    private final ReceiverService receiverService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<BloodRequest>> createRequest(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody BloodRequestDto dto) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Request created",
                        receiverService.createRequest(userId, dto)
                )
        );
    }

    @GetMapping("/search-donors")
    public ResponseEntity<ApiResponse<List<DonorProfile>>> searchDonors(
            @RequestParam BloodGroup bloodGroup,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "50.0") Double radius) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Donors found",
                        receiverService.searchNearbyDonors(
                                bloodGroup,
                                lat,
                                lon,
                                radius
                        )
                )
        );
    }

    @GetMapping("/search-hospitals")
    public ResponseEntity<ApiResponse<List<Hospital>>> searchHospitals(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "50.0") Double radius) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Hospitals found",
                        receiverService.searchNearbyHospitals(
                                lat,
                                lon,
                                radius
                        )
                )
        );
    }
    // --- ADD THIS NEW ENDPOINT ---
    @GetMapping("/requests/my-requests")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> getMyRequests(
            @AuthenticationPrincipal UserDetails user) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "My requests fetched successfully",
                        receiverService.getMyRequests(userId)
                )
        );
    }
}