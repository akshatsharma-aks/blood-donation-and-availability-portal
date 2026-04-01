package com.blooddonation.controller;

import com.blooddonation.entity.*;
import java.util.Map;
import com.blooddonation.dto.request.BloodInventoryDto;
import com.blooddonation.dto.response.ApiResponse;

import com.blooddonation.enums.VerificationStatus;

import com.blooddonation.service.HospitalService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HOSPITAL')")
@Tag(name = "Hospital APIs")
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping("/inventory")
    public ResponseEntity<ApiResponse<BloodInventory>> updateInventory(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody BloodInventoryDto dto) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Inventory updated",
                        hospitalService.updateInventory(userId, dto)
                )
        );
    }

    @PostMapping("/verify-donor")
    public ResponseEntity<ApiResponse<DonorProfile>> verifyDonor(
            @AuthenticationPrincipal UserDetails user, // Added this line
            @RequestParam Long donorId,
            @RequestParam VerificationStatus status) {

        Long userId = ((User) user).getId(); // Extract the logged-in hospital's User ID

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Donor verified",
                        hospitalService.verifyDonor(userId, donorId, status) // Pass it here
                )
        );
    }

    @PostMapping("/verify-donation")
    public ResponseEntity<ApiResponse<DonationHistory>> verifyDonation(
            @AuthenticationPrincipal UserDetails user, // Added this line
            @RequestParam Long historyId,
            @RequestParam VerificationStatus status) {

        Long userId = ((User) user).getId(); // Extract the logged-in hospital's User ID

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Donation verified",
                        hospitalService.verifyDonation(userId, historyId, status) // Pass it here
                )
        );
    }

    // ... inside HospitalController ...

    @GetMapping("/requests/nearby")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> viewNearbyRequests(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "15.0") double radiusKm) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Nearby requests fetched",
                        hospitalService.viewNearbyRequests(userId, radiusKm)
                )
        );
    }

    @GetMapping("/requests/direct")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> viewDirectRequests(
            @AuthenticationPrincipal UserDetails user) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Direct requests fetched",
                        hospitalService.viewDirectRequests(userId)
                )
        );
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<BloodRequest>> acceptRequest(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long requestId) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Blood request accepted successfully by Hospital/Blood Bank",
                        hospitalService.acceptRequest(userId, requestId)
                )
        );
    }
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Hospital>> getProfile(
            @AuthenticationPrincipal UserDetails user) {
        Long userId = ((User) user).getId();
        return ResponseEntity.ok(
                ApiResponse.ok("Hospital profile fetched", hospitalService.getProfile(userId))
        );
    }

    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<List<BloodInventory>>> getInventory(
            @AuthenticationPrincipal UserDetails user) {
        Long userId = ((User) user).getId();
        return ResponseEntity.ok(
                ApiResponse.ok("Inventory fetched", hospitalService.getInventory(userId))
        );
    }

    @GetMapping("/verifications/pending")
    public ResponseEntity<ApiResponse<List<VerificationRequest>>> getPendingVerifications(
            @AuthenticationPrincipal UserDetails user) {
        Long userId = ((User) user).getId();
        return ResponseEntity.ok(
                ApiResponse.ok("Pending verifications fetched", hospitalService.getPendingVerifications(userId))
        );
    }

    @PostMapping("/verifications/{requestId}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveVerification(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long requestId,
            @RequestBody Map<String, String> payload) { // Frontend sends { status: 'VERIFIED' }

        Long userId = ((User) user).getId();
        VerificationStatus status = VerificationStatus.valueOf(payload.get("status"));

        hospitalService.resolveVerification(userId, requestId, status);

        return ResponseEntity.ok(
                ApiResponse.ok("Verification resolved successfully", null)
        );
    }
}