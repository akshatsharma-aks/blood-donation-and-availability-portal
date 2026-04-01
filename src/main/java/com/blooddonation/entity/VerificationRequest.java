package com.blooddonation.entity;

import com.blooddonation.enums.VerificationStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donor_id")
    @JsonIgnore
    private DonorProfile donor;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    @JsonIgnore
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status = VerificationStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime requestedAt;

    private LocalDateTime resolvedAt;
}