package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlotResponse {
    private Long id;
    private String slotNumber;
    private String slotType;
    private String availabilityStatus;
    private Boolean chargingEnabled;
    private Long parkingAreaId;
    private String parkingAreaName;
}
