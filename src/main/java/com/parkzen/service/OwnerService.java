package com.parkzen.service;

import com.parkzen.dto.request.SlotRequest;
import com.parkzen.dto.response.*;

import java.util.List;

public interface OwnerService {
    OwnerAnalyticsResponse getDashboard();
    ParkingSlotResponse addSlot(SlotRequest request);
    ParkingSlotResponse updateSlot(Long slotId, SlotRequest request);
    void deleteSlot(Long slotId);
    ParkingSlotResponse updateSlotStatus(Long slotId, String status);
    List<BookingResponse> getOwnerBookings();
    List<PaymentResponse> getOwnerPayments();
    OwnerAnalyticsResponse getAnalytics();
    List<FeedbackResponse> getOwnerReviews();
}
