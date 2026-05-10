package com.parkzen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExtendBookingRequest {

    @NotNull(message = "Additional hours is required")
    @Min(value = 1, message = "Minimum extension is 1 hour")
    private Integer additionalHours;
}
