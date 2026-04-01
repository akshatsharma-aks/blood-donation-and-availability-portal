package com.blooddonation.controller;

import com.blooddonation.entity.*;

import com.blooddonation.dto.request.DonorProfileDto;
import com.blooddonation.dto.response.ApiResponse;

import com.blooddonation.service.DonorService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/donor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DONOR')")
@Tag(name = "Donor APIs")
public class DonorController {

    private final DonorService donorService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DonorProfile>> getProfile(
            @AuthenticationPrincipal UserDetails user) {

        Long userId = getUserId(user);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Profile fetched",
                        donorService.getProfile(userId)
                )
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DonorProfile>> updateProfile(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody DonorProfileDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Profile updated",
                        donorService.updateProfile(getUserId(user), dto)
                )
        );
    }

    @PostMapping("/upload-donation")
    public ResponseEntity<ApiResponse<DonationHistory>> uploadDonation(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("file") MultipartFile file,
            @RequestParam Long hospitalId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Donation uploaded",
                        donorService.uploadDonation(
                                getUserId(user),
                                file,
                                hospitalId,
                                date
                        )
                )
        );
    }

    @PostMapping("/verify-request")
    public ResponseEntity<ApiResponse<VerificationRequest>> requestVerification(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam Long hospitalId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Verification requested",
                        donorService.requestVerification(
                                getUserId(user),
                                hospitalId
                        )
                )
        );
    }

    private Long getUserId(UserDetails user) {
        return ((User) user).getId();
    }

//    @PutMapping("/profile")
//    public ResponseEntity<ApiResponse<DonorProfile>> updateProfile(
//            @AuthenticationPrincipal UserDetails user,
//            @RequestBody DonorProfileDto dto) {
//
//        Long userId = ((User) user).getId();
//        return ResponseEntity.ok(
//                ApiResponse.ok("Profile updated successfully", donorService.updateProfile(userId, dto))
//        );
//    }

    // --- NEW: View Nearby Requests ---
    @GetMapping("/requests/nearby")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> viewNearbyRequests(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(defaultValue = "10.0") double radiusKm) { // Default 10km search

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Nearby requests fetched",
                        donorService.viewNearbyRequests(userId, radiusKm)
                )
        );
    }

    // --- NEW: Accept a Request ---
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<ApiResponse<BloodRequest>> acceptRequest(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long requestId) {

        Long userId = ((User) user).getId();

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Blood request accepted successfully",
                        donorService.acceptRequest(userId, requestId)
                )
        );
    }
    // --- NEW: View Direct Targeted Requests ---
    @GetMapping("/requests/direct")
    public ResponseEntity<ApiResponse<List<BloodRequest>>> viewDirectRequests(
            @AuthenticationPrincipal UserDetails user) {

        Long userId = getUserId(user);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Direct requests fetched",
                        donorService.viewDirectRequests(userId)
                )
        );
    }
}