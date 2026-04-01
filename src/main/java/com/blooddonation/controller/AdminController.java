package com.blooddonation.controller;

import com.blooddonation.entity.Hospital;

import com.blooddonation.enums.VerificationStatus;
import java.util.List;
import com.blooddonation.entity.User;
import com.blooddonation.dto.response.ApiResponse;
import com.blooddonation.dto.response.DashboardStatsResponse;

import com.blooddonation.service.AdminService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin APIs")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/verify-hospital")
    public ResponseEntity<ApiResponse<Hospital>> verifyHospital(
            @RequestParam Long hospitalId,
            @RequestParam VerificationStatus status) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Hospital status updated",
                        adminService.verifyHospital(hospitalId, status)
                )
        );
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboard() {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Dashboard stats",
                        adminService.getDashboardStats()
                )
        );
    }

    // Make sure you have this import:
    // import org.springframework.web.bind.annotation.PathVariable;

    @PutMapping("/users/{userId}/suspend")
    public ResponseEntity<ApiResponse<String>> suspendUser(@PathVariable Long userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.ok(
                ApiResponse.ok("User suspended successfully. They can no longer access the platform.", null)
        );
    }

    @PutMapping("/users/{userId}/reactivate")
    public ResponseEntity<ApiResponse<String>> reactivateUser(@PathVariable Long userId) {
        adminService.reactivateUser(userId);
        return ResponseEntity.ok(
                ApiResponse.ok("User reactivated successfully.", null)
        );
    }
    // --- ADD THESE NEW ENDPOINTS ---

    @GetMapping("/hospitals/pending")
    public ResponseEntity<ApiResponse<List<Hospital>>> getPendingHospitals() {
        return ResponseEntity.ok(
                ApiResponse.ok("Pending hospitals fetched", adminService.getPendingHospitals())
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(
                ApiResponse.ok("All users fetched", adminService.getAllUsers())
        );
    }
}