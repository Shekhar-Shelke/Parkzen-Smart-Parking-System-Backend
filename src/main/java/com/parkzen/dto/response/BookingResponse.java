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
public class BookingResponse {
    private Long id;
    private LocalDateTime bookingTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double durationHours;
    private Double totalAmount;
    private String bookingStatus;
    private String qrCode;
    private Boolean extendable;
    private Long userId;
    private String userName;
    private Long slotId;
    private String slotNumber;
    private Long parkingAreaId;
    private String parkingAreaName;
    private String parkingAreaAddress;
    private PaymentResponse payment;
}
