package com.blooddonation.entity;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.RequestStatus;
import com.blooddonation.enums.UrgencyLevel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

//    @ManyToOne
//    @JoinColumn(name = "accepted_by_donor_id")
//    private DonorProfile acceptedByDonor;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private Integer unitsRequired;
    private String hospitalName;
    private String location;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgencyLevel;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // The donor who accepted the general request (we added this earlier)
    @ManyToOne
    @JoinColumn(name = "accepted_by_donor_id")
    @JsonIgnore
    private DonorProfile acceptedByDonor;

    // The hospital/blood bank that fulfills the request
    @ManyToOne
    @JoinColumn(name = "accepted_by_hospital_id")
    @JsonIgnore
    private Hospital acceptedByHospital;

    // --- NEW: TARGETED REQUESTS ---

    // If the receiver targets a specific donor
    @ManyToOne
    @JoinColumn(name = "target_donor_id")
    @JsonIgnore
    private DonorProfile targetDonor;

    // If the receiver targets a specific hospital
    @ManyToOne
    @JoinColumn(name = "target_hospital_id")
    @JsonIgnore
    private Hospital targetHospital;
}