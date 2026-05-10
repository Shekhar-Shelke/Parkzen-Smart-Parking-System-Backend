package com.parkzen.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AlertRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private Long parkingAreaId;
}
