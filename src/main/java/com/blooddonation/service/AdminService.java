package com.blooddonation.service;

import com.blooddonation.entity.Hospital;

import com.blooddonation.entity.User;
import com.blooddonation.enums.VerificationStatus;
import com.blooddonation.enums.RequestStatus;

import com.blooddonation.dto.response.DashboardStatsResponse;

import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.UserRepository;
import com.blooddonation.repository.DonorProfileRepository;
import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.DonationHistoryRepository;
import com.blooddonation.repository.BloodInventoryRepository;

import com.blooddonation.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final HospitalRepository hospitalRepo;
    private final UserRepository userRepo;
    private final DonorProfileRepository donorRepo;
    private final BloodRequestRepository requestRepo;
    private final DonationHistoryRepository historyRepo;
    private final BloodInventoryRepository inventoryRepo;

    public Hospital verifyHospital(Long hospitalId, VerificationStatus status) {

        Hospital hospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hospital not found"));

        hospital.setVerificationStatus(status);

        return hospitalRepo.save(hospital);
    }

    public DashboardStatsResponse getDashboardStats() {

        long totalDonors = donorRepo.count();

        long totalHospitals = hospitalRepo
                .findByVerificationStatus(VerificationStatus.VERIFIED)
                .size();

        long activeRequests = requestRepo
                .countByRequestStatus(RequestStatus.PENDING);

        long donationRecords = historyRepo.count();

        long totalUnits = inventoryRepo.findAll()
                .stream()
                .mapToLong(i -> i.getUnitsAvailable())
                .sum();

        return DashboardStatsResponse.builder()
                .totalDonors(totalDonors)
                .totalHospitals(totalHospitals)
                .activeBloodRequests(activeRequests)
                .donationRecords(donationRecords)
                .availableBloodUnits(totalUnits)
                .build();
    }

    // Make sure to import com.blooddonation.entity.User if it isn't already
    // import com.blooddonation.entity.User;

    /**
     * Suspend a user (Donor, Receiver, or Hospital).
     * This sets their enabled status to false, preventing them from logging in
     * and effectively removing them from active platform operations.
     */
    public void suspendUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("User is already suspended.");
        }

        user.setEnabled(false);
        userRepo.save(user);
    }

    /**
     * Optional but highly recommended: A way to reactivate a user
     * in case they were suspended by mistake.
     */
    public void reactivateUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("User is already active.");
        }

        user.setEnabled(true);
        userRepo.save(user);
    }
    // --- ADD THESE NEW METHODS ---

    public java.util.List<Hospital> getPendingHospitals() {
        // Assuming your HospitalRepository has this Spring Data JPA method.
        // If not, it will work automatically if the property is named 'verificationStatus'.
        return hospitalRepo.findByVerificationStatus(VerificationStatus.PENDING);
    }

    public java.util.List<User> getAllUsers() {
        return userRepo.findAll();
    }
}