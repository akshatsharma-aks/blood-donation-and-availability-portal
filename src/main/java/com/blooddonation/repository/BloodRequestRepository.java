package com.blooddonation.repository;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.RequestStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.blooddonation.entity.BloodRequest;
import com.blooddonation.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findByReceiverId(Long receiverId);

    List<BloodRequest> findByBloodGroupAndRequestStatus(
            BloodGroup bloodGroup,
            RequestStatus status
    );

//    @Query(value = """
//        SELECT r.* FROM blood_requests r
//        WHERE r.request_status = 'PENDING'
//        AND (6371 * acos(
//          cos(radians(:lat)) * cos(radians(r.latitude)) *
//          cos(radians(r.longitude) - radians(:lon)) +
//          sin(radians(:lat)) * sin(radians(r.latitude))
//        )) < :radius
//        """, nativeQuery = true)
//    List<BloodRequest> findNearbyPendingRequests(
//            @Param("lat") Double lat,
//            @Param("lon") Double lon,
//            @Param("radius") Double radius);

    long countByRequestStatus(RequestStatus status);

    // 1. UPDATED: Find ALL nearby PENDING requests (Broadcasts + Targeted)
    @Query(value = "SELECT * FROM blood_requests b WHERE b.blood_group = :bloodGroup " +
            "AND b.request_status = 'PENDING' " +
            "AND (6371 * acos(cos(radians(:lat)) * cos(radians(b.latitude)) * cos(radians(b.longitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(b.latitude)))) <= :radius", nativeQuery = true)
    List<BloodRequest> findNearbyPendingRequests(
            @Param("bloodGroup") String bloodGroup,
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radius") double radiusKm
    );

    // 2. Keep this exactly as it is:
    List<BloodRequest> findByTargetDonorIdAndRequestStatus(Long targetDonorId, RequestStatus status);

    // 1. Find ALL nearby PENDING requests regardless of blood group (For Hospitals)
    @Query(value = "SELECT * FROM blood_requests b WHERE b.request_status = 'PENDING' AND " +
            "(6371 * acos(cos(radians(:lat)) * cos(radians(b.latitude)) * cos(radians(b.longitude) - radians(:lon)) + " +
            "sin(radians(:lat)) * sin(radians(b.latitude)))) <= :radius", nativeQuery = true)
    List<BloodRequest> findAllNearbyPendingRequests(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radius") double radiusKm
    );

    // 2. Find requests targeted directly at a specific hospital
    List<BloodRequest> findByTargetHospitalIdAndRequestStatus(Long targetHospitalId, RequestStatus status);
}