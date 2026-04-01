package com.blooddonation.entity;

import com.blooddonation.enums.VerificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospitals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Hospital {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)

    private Long id;



    @OneToOne @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;



    private String hospitalName;

    private String licenseNumber;

    private String address;

    private Double latitude;

    private Double longitude;

    private String city;
    private String state;


    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus            = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private com.blooddonation.enums.InstitutionType institutionType = com.blooddonation.enums.InstitutionType.HOSPITAL;


    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)

    private List<BloodInventory> inventory = new ArrayList<>();

}
