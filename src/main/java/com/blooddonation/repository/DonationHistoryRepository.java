package com.blooddonation.repository;



import com.blooddonation.entity.DonationHistory;

import com.blooddonation.enums.VerificationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;



@Repository

public interface DonationHistoryRepository

        extends JpaRepository<DonationHistory, Long> {



    /** All donation records for a specific donor. */

    List<DonationHistory> findByDonorId(Long donorId);



    /** Verified records for a donor — shown on public profile. */

    List<DonationHistory> findByDonorIdAndVerificationStatus(

            Long donorId, VerificationStatus status);



    /**

     * All records verified by a particular hospital.

     * Used by HospitalController to list what it has approved.

     */

    List<DonationHistory> findByHospitalId(Long hospitalId);



    /**

     * Pending verification records at a hospital.

     * Hospital sees these and can approve or reject.

     */

    List<DonationHistory> findByHospitalIdAndVerificationStatus(

            Long hospitalId, VerificationStatus status);



    /** Count for admin dashboard — total donation records. */

    long count();



    /** Count verified donations — shows trust level of platform. */

    long countByVerificationStatus(VerificationStatus status);



    /**

     * Checks if donor already has an UNVERIFIED record at this

     * hospital to avoid duplicates.

     */

    @Query(""" 

        SELECT COUNT(d) > 0 FROM DonationHistory d 

        WHERE d.donor.id = :donorId 

          AND d.hospital.id = :hospitalId 

          AND d.verificationStatus = 'UNVERIFIED' 

        """)

    boolean existsPendingRecord(

            @Param("donorId")   Long donorId,

            @Param("hospitalId") Long hospitalId);

}