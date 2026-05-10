package com.parkzen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotRequest {

    @NotNull(message = "Parking area ID is required")
    private Long parkingAreaId;

    @NotBlank(message = "Slot number is required")
    private String slotNumber;

    private String slotType = "CAR";

    private Boolean chargingEnabled = false;
}
