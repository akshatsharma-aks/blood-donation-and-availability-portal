package com.blooddonation.repository;

import com.blooddonation.entity.DonorProfile;
import com.blooddonation.enums.BloodGroup;

import com.blooddonation.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {

    Optional<DonorProfile> findByUserId(Long userId);

    List<DonorProfile> findByBloodGroup(BloodGroup bloodGroup);

    @Query(value = """
        SELECT d.* FROM donor_profiles d
        WHERE d.blood_group = :bg
        AND d.verification_status = 'VERIFIED'
        AND (6371 * acos(
          cos(radians(:lat)) * cos(radians(d.latitude)) *
          cos(radians(d.longitude) - radians(:lon)) +
          sin(radians(:lat)) * sin(radians(d.latitude))
        )) < :radius
        """, nativeQuery = true)
    List<DonorProfile> findNearbyDonors(
            @Param("bg") String bloodGroup,
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radius") Double radiusKm);
    List<DonorProfile> findByVerificationStatusAndVerificationExpiryDateBefore(
            VerificationStatus status,
            LocalDate date
    );
    // Find verified donors of a specific blood group in a specific city
    List<DonorProfile> findByBloodGroupAndVerificationStatusAndCityIgnoreCase(
            com.blooddonation.enums.BloodGroup bloodGroup,
            com.blooddonation.enums.VerificationStatus status,
            String city
    );
}