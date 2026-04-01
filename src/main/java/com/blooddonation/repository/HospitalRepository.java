package com.blooddonation.repository;

import com.blooddonation.entity.Hospital;
import com.blooddonation.enums.VerificationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByUserId(Long userId);

    List<Hospital> findByVerificationStatus(VerificationStatus status);

    @Query(value = """
        SELECT h.* FROM hospitals h
        WHERE h.verification_status = 'VERIFIED'
        AND (6371 * acos(
          cos(radians(:lat)) * cos(radians(h.latitude)) *
          cos(radians(h.longitude) - radians(:lon)) +
          sin(radians(:lat)) * sin(radians(h.latitude))
        )) < :radius
        """, nativeQuery = true)
    List<Hospital> findNearbyHospitals(
            @Param("lat") Double latitude,
            @Param("lon") Double longitude,
            @Param("radius") Double radiusKm);
    // Find verified hospitals in a specific city
    List<Hospital> findByVerificationStatusAndCityIgnoreCase(
            com.blooddonation.enums.VerificationStatus status,
            String city
    );
}