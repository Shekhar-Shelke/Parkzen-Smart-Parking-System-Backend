package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingAreaResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer totalSlots;
    private Integer availableSlots;
    private Double pricePerHour;
    private Boolean chargingAvailable;
    private Double chargingPricePerHour;
    private String status;
    private Long ownerId;
    private String ownerName;
    private Double distanceKm;
}
