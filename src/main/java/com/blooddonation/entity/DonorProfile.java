package com.blooddonation.entity;

import com.blooddonation.enums.BloodGroup;
import com.blooddonation.enums.VerificationStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    private LocalDate availableFromDate;
    private LocalDate availableToDate;

    private String phone;
    private Double latitude;
    private Double longitude;
    private LocalDate lastDonationDate;
    private String city;
    private String state;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus
            = VerificationStatus.UNVERIFIED;

    private LocalDate verificationExpiryDate;



    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DonationHistory> donationHistory = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private com.blooddonation.enums.Willingness willingness;
}
