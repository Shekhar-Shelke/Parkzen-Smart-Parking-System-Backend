package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerAnalyticsResponse {
    private Long totalBookings;
    private Double totalEarnings;
    private Double todayEarnings;
    private Double monthlyEarnings;
    private Double slotUsagePercentage;
    private Integer totalSlots;
    private Integer availableSlots;
    private Integer bookedSlots;
    private Long totalReviews;
    private Double averageRating;
}
