package com.parkzen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {

    @NotNull(message = "Parking area ID is required")
    private Long parkingAreaId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "Duration hours is required")
    @Min(value = 1, message = "Minimum booking duration is 1 hour")
    private Integer durationHours;

    private Boolean requestCharging = false;
}
