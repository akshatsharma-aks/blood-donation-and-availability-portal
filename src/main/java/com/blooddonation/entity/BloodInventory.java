package com.blooddonation.entity;

import com.blooddonation.enums.BloodGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blood_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    @JsonIgnore
    private Hospital hospital;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private Integer unitsAvailable;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}