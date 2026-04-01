package com.blooddonation.dto.request;



import com.blooddonation.enums.BloodGroup;

import jakarta.validation.constraints.*;

import lombok.Data;



@Data

public class BloodInventoryDto {



    @NotNull(message = "Blood group is required")

    private BloodGroup bloodGroup;



    @NotNull(message = "Units available is required")

    @Min(value = 0, message = "Units cannot be negative")

    @Max(value = 10000, message = "Units value seems unrealistic")

    private Integer unitsAvailable;



    /**

     * Optional note, e.g. expiry info or source of the units.

     */

    @Size(max = 500)

    private String note;

}