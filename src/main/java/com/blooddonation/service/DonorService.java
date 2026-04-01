package com.blooddonation.service;

import com.blooddonation.entity.*;

import com.blooddonation.dto.request.DonorProfileDto;

import com.blooddonation.enums.VerificationStatus;
import com.blooddonation.exception.BusinessException;
import com.blooddonation.repository.*;

import com.blooddonation.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DonorService {

    private final DonorProfileRepository donorRepo;
    private final DonationHistoryRepository historyRepo;
    private final BloodRequestRepository requestRepo;
    private final VerificationRequestRepository verifyRepo;
    private final FileStorageService fileStorage;
    private final HospitalRepository hospitalRepo;

    public DonorProfile getProfile(Long userId) {

        return donorRepo.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Donor profile not found"));
    }

    public DonorProfile updateProfile(Long userId, DonorProfileDto dto) {
        DonorProfile profile = getProfile(userId);

        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setLatitude(dto.getLatitude());
        profile.setLongitude(dto.getLongitude());
        profile.setCity(dto.getCity());     // From previous fix
        profile.setState(dto.getState());   // From previous fix
        profile.setLastDonationDate(dto.getLastDonationDate());

        // NEW: Map the availability fields
        profile.setAvailableFromDate(dto.getAvailableFromDate());
        profile.setAvailableToDate(dto.getAvailableToDate());
        profile.setWillingness(dto.getWillingness());

        return donorRepo.save(profile);
    }

    public DonationHistory uploadDonation(
            Long userId,
            MultipartFile file,
            Long hospitalId, // This was passed but never used to fetch the hospital!
            LocalDate date) {

        DonorProfile donor = getProfile(userId);

        // Fetch the hospital correctly inside the method
        Hospital targetHospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        String url = fileStorage.store(file);

        DonationHistory history = DonationHistory.builder()
                .donor(donor)
                .hospital(targetHospital) // Use the locally fetched hospital
                .date(date)
                .certificateFileUrl(url)
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .build();

        return historyRepo.save(history);
    }

    public VerificationRequest requestVerification(Long userId, Long hospitalId) {

        DonorProfile donor = getProfile(userId);

        Hospital hospital = hospitalRepo.findById(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found"));

        if (verifyRepo.existsPendingRequest(donor.getId(), hospitalId)) {
            throw new BusinessException("Verification request already pending");
        }

        VerificationRequest request = VerificationRequest.builder()
                .donor(donor)
                .hospital(hospital)
                .status(VerificationStatus.PENDING)
                .build();

        return verifyRepo.save(request);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void expireOldVerifications() {
        // Find everyone who is Verified but their expiry date is before today
        List<DonorProfile> expiredDonors = donorRepo
                .findByVerificationStatusAndVerificationExpiryDateBefore(
                        VerificationStatus.VERIFIED,
                        LocalDate.now()
                );

        if (!expiredDonors.isEmpty()) {
            expiredDonors.forEach(donor -> {
                donor.setVerificationStatus(VerificationStatus.EXPIRED);
                // We keep the old date so we know *when* they expired
            });

            // Save them all in one batch
            donorRepo.saveAll(expiredDonors);

            // Optional: You can use a logger here if you have @Slf4j on the class
            System.out.println("System Job: Expired " + expiredDonors.size() + " donor verifications today.");
        }
    }
    /**
     * View nearby PENDING requests matching the donor's blood group.
     */
    public List<BloodRequest> viewNearbyRequests(Long userId, double radiusKm) {
        DonorProfile donor = getProfile(userId);

        if (donor.getLatitude() == null || donor.getLongitude() == null) {
            throw new RuntimeException("Donor location is not set. Please update profile.");
        }

        return requestRepo.findNearbyPendingRequests(
                donor.getBloodGroup().name(),
                donor.getLatitude(),
                donor.getLongitude(),
                radiusKm
        );
    }

    /**
     * Accept a pending blood request.
     */
    public BloodRequest acceptRequest(Long userId, Long requestId) {
        DonorProfile donor = getProfile(userId);

        BloodRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found"));

        if (request.getRequestStatus() != com.blooddonation.enums.RequestStatus.PENDING) {
            throw new RuntimeException("This request is no longer available.");
        }

        // Link the donor and update the status
        request.setAcceptedByDonor(donor);
        request.setRequestStatus(com.blooddonation.enums.RequestStatus.ACCEPTED);

        return requestRepo.save(request);
    }
    /**
     * View requests targeted explicitly at this donor.
     */
    public List<BloodRequest> viewDirectRequests(Long userId) {
        DonorProfile donor = getProfile(userId);
        return requestRepo.findByTargetDonorIdAndRequestStatus(
                donor.getId(),
                com.blooddonation.enums.RequestStatus.PENDING
        );
    }
}