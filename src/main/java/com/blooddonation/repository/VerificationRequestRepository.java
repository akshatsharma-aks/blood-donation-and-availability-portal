package com.blooddonation.repository;



import com.blooddonation.entity.VerificationRequest;

import com.blooddonation.enums.VerificationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;



@Repository

public interface VerificationRequestRepository

        extends JpaRepository<VerificationRequest, Long> {



    /** All verification requests sent to a specific hospital. */

    List<VerificationRequest> findByHospitalId(Long hospitalId);



    /** Pending requests at a hospital — hospital action queue. */

    List<VerificationRequest> findByHospitalIdAndStatus(

            Long hospitalId, VerificationStatus status);



    /** All requests submitted by a donor. */

    List<VerificationRequest> findByDonorId(Long donorId);



    /**

     * Find a specific donor-hospital pair.

     * Used to prevent duplicate verification requests.

     */

    Optional<VerificationRequest> findByDonorIdAndHospitalIdAndStatus(

            Long donorId, Long hospitalId, VerificationStatus status);



    /**

     * Checks if an active (PENDING) request already exists

     * between this donor and hospital combination.

     */

    @Query(""" 

        SELECT COUNT(v) > 0 FROM VerificationRequest v 

        WHERE v.donor.id = :donorId 

          AND v.hospital.id = :hospitalId 

          AND v.status = 'PENDING' 

        """)

    boolean existsPendingRequest(

            @Param("donorId")   Long donorId,

            @Param("hospitalId") Long hospitalId);



    /** Admin dashboard — count unresolved hospital verifications. */

    long countByStatus(VerificationStatus status);

}