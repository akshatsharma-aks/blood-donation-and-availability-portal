package com.blooddonation.service;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.entity.DonorProfile;
import com.blooddonation.entity.Hospital;
import com.blooddonation.entity.User;

import com.blooddonation.dto.request.BloodRequestDto;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.RequestStatus;

import com.blooddonation.repository.BloodRequestRepository;
import com.blooddonation.repository.DonorProfileRepository;
import com.blooddonation.repository.HospitalRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.blooddonation.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiverService {

    private final BloodRequestRepository requestRepo;
    private final DonorProfileRepository donorRepo;
    private final HospitalRepository hospitalRepo;

    public BloodRequest createRequest(Long userId, BloodRequestDto dto) {

        User receiver = new User();
        receiver.setId(userId);

        BloodRequest.BloodRequestBuilder reqBuilder = BloodRequest.builder()
                .receiver(receiver)
                .bloodGroup(dto.getBloodGroup())
                .unitsRequired(dto.getUnitsRequired())
                .hospitalName(dto.getHospitalName())
                .location(dto.getLocation())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .urgencyLevel(dto.getUrgencyLevel())
                .requestStatus(RequestStatus.PENDING);

        // --- NEW: Handle Targeted Requests ---

        if (dto.getTargetDonorId() != null) {
            DonorProfile targetDonor = donorRepo.findById(dto.getTargetDonorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target Donor not found"));
            reqBuilder.targetDonor(targetDonor);
        }

        if (dto.getTargetHospitalId() != null) {
            Hospital targetHospital = hospitalRepo.findById(dto.getTargetHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target Hospital not found"));
            reqBuilder.targetHospital(targetHospital);
        }

        return requestRepo.save(reqBuilder.build());
    }

    public List<DonorProfile> searchNearbyDonors(
            BloodGroup bg,
            Double lat,
            Double lon,
            Double radius) {

        return donorRepo.findNearbyDonors(
                bg.name(),
                lat,
                lon,
                radius
        );
    }

    public List<Hospital> searchNearbyHospitals(
            Double lat,
            Double lon,
            Double radius) {

        return hospitalRepo.findNearbyHospitals(
                lat,
                lon,
                radius
        );
    }
    // --- ADD THIS NEW METHOD ---
    public List<BloodRequest> getMyRequests(Long userId) {
        // Assuming your BloodRequestRepository has this standard Spring Data JPA method.
        // If it doesn't, add: List<BloodRequest> findByReceiverId(Long receiverId); to the repo.
        return requestRepo.findByReceiverId(userId);
    }
}