package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {
    private Long id;
    private String name;
    private String parkingAreaName;
    private String address;
    private Double latitude;
    private Double longitude;
    private String email;
    private Boolean approved;
    private String role;
    private LocalDateTime createdAt;
}
