package com.blooddonation.service;



import com.blooddonation.dto.response.DonorProfileResponse;

import com.blooddonation.dto.response.HospitalProfileResponse;

import com.blooddonation.entity.DonorProfile;

import com.blooddonation.entity.Hospital;

import com.blooddonation.enums.BloodGroup;

import com.blooddonation.repository.BloodRequestRepository;

import com.blooddonation.repository.DonorProfileRepository;

import com.blooddonation.repository.HospitalRepository;

import com.blooddonation.util.HaversineUtil;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;



@Slf4j

@Service

@RequiredArgsConstructor

@Transactional(readOnly = true)

public class LocationService {



    private final DonorProfileRepository donorRepository;

    private final HospitalRepository     hospitalRepository;

    private final BloodRequestRepository requestRepository;



    /** Default search radius when caller does not specify one. */

    private static final double DEFAULT_RADIUS_KM = 50.0;



    // ── Donor Search ────────────────────────────────────────────



    /**

     * Find verified donors within radiusKm that have the

     * requested blood group. Results are sorted nearest-first.

     *

     * @param bloodGroup  required blood group

     * @param lat         searcher's latitude

     * @param lon         searcher's longitude

     * @param radiusKm    search radius in kilometres

     * @return            list of donors with distance attached

     */

    public List<DonorProfileResponse> findNearbyDonors(

            BloodGroup bloodGroup,

            double lat, double lon,

            double radiusKm) {



        log.debug("Searching donors: group={}, lat={}, lon={}, r={}km",

                bloodGroup, lat, lon, radiusKm);



        List<DonorProfile> donors = donorRepository.findNearbyDonors(

                bloodGroup.name(), lat, lon, radiusKm);



        return donors.stream()

                .map(d -> {

                    double dist = HaversineUtil.distance(

                            lat, lon,

                            d.getLatitude(), d.getLongitude());

                    return DonorProfileResponse.from(d, dist);

                })

                .sorted((a, b) ->

                        Double.compare(a.getDistanceKm(),

                                b.getDistanceKm()))

                .collect(Collectors.toList());

    }



    /**

     * Find all verified donors with the given blood group,

     * regardless of location (global search mode).

     */

    public List<DonorProfileResponse> findAllDonorsByBloodGroup(

            BloodGroup bloodGroup) {

        return donorRepository.findByBloodGroup(bloodGroup)

                .stream()

                .map(d -> DonorProfileResponse.from(d, null))

                .collect(Collectors.toList());

    }



    // ── Hospital Search ─────────────────────────────────────────



    /**

     * Find verified hospitals within radiusKm of the caller.

     * Results are sorted nearest-first.

     *

     * @param lat       searcher's latitude

     * @param lon       searcher's longitude

     * @param radiusKm  search radius in kilometres

     * @return          list of hospitals with distance attached

     */

    public List<HospitalProfileResponse> findNearbyHospitals(

            double lat, double lon, double radiusKm) {



        log.debug("Searching hospitals: lat={}, lon={}, r={}km",

                lat, lon, radiusKm);



        List<Hospital> hospitals =

                hospitalRepository.findNearbyHospitals(lat, lon, radiusKm);



        return hospitals.stream()

                .map(h -> {

                    double dist = HaversineUtil.distance(

                            lat, lon,

                            h.getLatitude(), h.getLongitude());

                    return HospitalProfileResponse.from(h, dist);

                })

                .sorted((a, b) ->

                        Double.compare(a.getDistanceKm(),

                                b.getDistanceKm()))

                .collect(Collectors.toList());

    }



    /**

     * Global hospital search — no location filter.

     * Returns all VERIFIED hospitals ordered by name.

     */

    public List<HospitalProfileResponse> findAllVerifiedHospitals() {

        return hospitalRepository

                .findByVerificationStatus(

                        com.blooddonation.enums.VerificationStatus.VERIFIED)

                .stream()

                .map(h -> HospitalProfileResponse.from(h, null))

                .collect(Collectors.toList());

    }
    // ── City Search (New Feature) ────────────────────────────────

    /**
     * Find verified donors by exact city match (Case Insensitive).
     */
    public List<DonorProfileResponse> findDonorsInSameCity(
            BloodGroup bloodGroup,
            String city) {

        log.debug("Searching donors in city: {}", city);

        List<DonorProfile> donors = donorRepository
                .findByBloodGroupAndVerificationStatusAndCityIgnoreCase(
                        bloodGroup,
                        com.blooddonation.enums.VerificationStatus.VERIFIED,
                        city
                );

        // We pass 'null' for distance since this is a city-wide search, not a radius search
        return donors.stream()
                .map(d -> DonorProfileResponse.from(d, null))
                .collect(Collectors.toList());
    }

    /**
     * Find verified hospitals by exact city match (Case Insensitive).
     */
    public List<HospitalProfileResponse> findHospitalsInSameCity(String city) {

        log.debug("Searching hospitals in city: {}", city);

        List<Hospital> hospitals = hospitalRepository
                .findByVerificationStatusAndCityIgnoreCase(
                        com.blooddonation.enums.VerificationStatus.VERIFIED,
                        city
                );

        return hospitals.stream()
                .map(h -> HospitalProfileResponse.from(h, null))
                .collect(Collectors.toList());
    }

}