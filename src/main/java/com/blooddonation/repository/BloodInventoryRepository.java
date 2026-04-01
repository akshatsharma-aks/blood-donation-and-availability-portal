package com.blooddonation.repository;

import com.blooddonation.entity.BloodInventory;
import com.blooddonation.enums.BloodGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {

    Optional<BloodInventory> findByHospitalIdAndBloodGroup(
            Long hospitalId,
            BloodGroup bloodGroup
    );

    List<BloodInventory> findByHospitalId(Long hospitalId);

}