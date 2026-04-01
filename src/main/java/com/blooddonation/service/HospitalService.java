package com.blooddonation.service;

import com.blooddonation.entity.*;

import com.blooddonation.dto.request.BloodInventoryDto;

import com.blooddonation.enums.VerificationStatus;
import com.blooddonation.exception.UnauthorizedException; // or BusinessException depending on your setup
import com.blooddonation.repository.HospitalRepository;
import com.blooddonation.repository.BloodInventoryRepository;
import com.blooddonation.repository.DonorProfileRepository;
import com.blooddonation.repository.DonationHistoryRepository;
import com.blooddonation.repository.BloodRequestRepository;

import com.blooddonation.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalService {

    private final HospitalRepository hospitalRepo;
    private final BloodInventoryRepository inventoryRepo;
    private final DonorProfileRepository donorRepo;
    private final DonationHistoryRepository historyRepo;
    private final BloodRequestRepository requestRepo;
    private final com.blooddonation.repository.VerificationRequestRepository verifyRepo;

    public BloodInventory updateInventory(Long userId, BloodInventoryDto dto) {

        Hospital hospital = hospitalRepo.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hospital not found"));

        BloodInventory inv = inventoryRepo
                .findByHospitalIdAndBloodGroup(
                        hospital.getId(),
                        dto.getBloodGroup()
                )
                .orElse(
                        BloodInventory.builder()
                                .hospital(hospital)
                                .bloodGroup(dto.getBloodGroup())
                                .build()
                );

        inv.setUnitsAvailable(dto.getUnitsAvailable());

        return inventoryRepo.save(inv);
    }

    public DonorProfile verifyDonor(Long hospitalUserId, Long donorId, VerificationStatus status) {
        // 1. SECURITY CHECK: Verify the hospital is authorized
        Hospital hospital = hospitalRepo.findByUserId(hospitalUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (hospital.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new RuntimeException("SECURITY ALERT: Only Admin-verified hospitals can verify donors.");
            // Replace RuntimeException with your BusinessException/UnauthorizedException
        }

        // 2. Proceed with Donor Verification
        DonorProfile donor = donorRepo.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found"));

        donor.setVerificationStatus(status);

        if (status == VerificationStatus.VERIFIED) {
            donor.setVerificationExpiryDate(LocalDate.now().plusDays(90));
        }

        return donorRepo.save(donor);
    }

    public DonationHistory verifyDonation(Long hospitalUserId, Long historyId, VerificationStatus status) {
        // 1. SECURITY CHECK: Verify the hospital is authorized
        Hospital hospital = hospitalRepo.findByUserId(hospitalUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (hospital.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new RuntimeException("SECURITY ALERT: Only Admin-verified hospitals can verify donation records.");
        }

        // 2. Proceed with Donation Verification
        DonationHistory history = historyRepo.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation history not found"));

        history.setVerificationStatus(status);

        return historyRepo.save(history);
    }
    /**
     * View all nearby PENDING requests.
     */
    public List<BloodRequest> viewNearbyRequests(Long userId, double radiusKm) {
        Hospital hospital = hospitalRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (hospital.getLatitude() == null || hospital.getLongitude() == null) {
            throw new RuntimeException("Hospital location is not set.");
        }

        return requestRepo.findAllNearbyPendingRequests(
                hospital.getLatitude(),
                hospital.getLongitude(),
                radiusKm
        );
    }

    /**
     * View requests targeted explicitly at this hospital/blood bank.
     */
    public List<BloodRequest> viewDirectRequests(Long userId) {
        Hospital hospital = hospitalRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        return requestRepo.findByTargetHospitalIdAndRequestStatus(
                hospital.getId(),
                com.blooddonation.enums.RequestStatus.PENDING
        );
    }

    /**
     * Accept/Fulfill a blood request.
     */
    public BloodRequest acceptRequest(Long userId, Long requestId) {
        Hospital hospital = hospitalRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        BloodRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found"));

        if (request.getRequestStatus() != com.blooddonation.enums.RequestStatus.PENDING) {
            throw new RuntimeException("This request is no longer available.");
        }

        // Link the hospital and update the status
        request.setAcceptedByHospital(hospital);
        request.setRequestStatus(com.blooddonation.enums.RequestStatus.ACCEPTED);

        return requestRepo.save(request);
    }
    // --- ADD THESE NEW METHODS ---

    // 1. Get Profile
    public Hospital getProfile(Long userId) {
        return hospitalRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));
    }

    // 2. Get Inventory
    public List<BloodInventory> getInventory(Long userId) {
        Hospital hospital = getProfile(userId);
        // Since Hospital has a @OneToMany relationship with Inventory, we can just return it
        return hospital.getInventory();
    }

    // 3. Get Pending Verifications assigned to this hospital
    public List<VerificationRequest> getPendingVerifications(Long userId) {
        Hospital hospital = getProfile(userId);
        return verifyRepo.findByHospitalIdAndStatus(hospital.getId(), VerificationStatus.PENDING);
    }

    // 4. Resolve a Verification Request (Approving or Rejecting)
    public void resolveVerification(Long userId, Long requestId, VerificationStatus status) {
        Hospital hospital = getProfile(userId);

        VerificationRequest request = verifyRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Verification request not found"));

        // Ensure this hospital is the one assigned to the request
        if (!request.getHospital().getId().equals(hospital.getId())) {
            throw new RuntimeException("Unauthorized to resolve this request.");
        }

        // Update Request Status
        request.setStatus(status);
        verifyRepo.save(request);

        // Update the Donor's Profile Status
        DonorProfile donor = request.getDonor();
        donor.setVerificationStatus(status);

        if (status == VerificationStatus.VERIFIED) {
            donor.setVerificationExpiryDate(java.time.LocalDate.now().plusDays(90));
        }
        donorRepo.save(donor);
    }
}