package com.blooddonation.dto.response;



import lombok.Builder;

import lombok.Data;

import java.time.LocalDateTime;

import java.util.Map;



@Data

@Builder

public class DashboardStatsResponse {



    // ── User Counts ─────────────────────────────────────────────

    private long totalDonors;

    private long totalReceivers;

    private long totalHospitals;

    private long pendingHospitalVerifications;



    // ── Blood Requests ──────────────────────────────────────────

    private long activeBloodRequests;    // status = PENDING

    private long fulfilledRequests;      // status = FULFILLED

    private long totalBloodRequestsEver;



    // ── Donation Records ────────────────────────────────────────

    private long donationRecords;

    private long verifiedDonationRecords;

    private long pendingDonationVerifications;



    // ── Inventory ───────────────────────────────────────────────

    /** Total blood units available across ALL hospitals. */

    private long availableBloodUnits;



    /**

     * Breakdown of total available units per blood group.

     * e.g. { "A_POS": 340, "O_NEG": 78 }

     */

    private Map<String, Long> unitsByBloodGroup;



    // ── Meta ────────────────────────────────────────────────────

    /** Timestamp when these stats were computed. */

    private LocalDateTime generatedAt;

}