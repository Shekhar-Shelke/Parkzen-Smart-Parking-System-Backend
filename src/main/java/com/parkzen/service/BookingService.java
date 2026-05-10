package com.parkzen.service;

import com.parkzen.dto.request.BookingRequest;
import com.parkzen.dto.request.ExtendBookingRequest;
import com.parkzen.dto.response.BookingResponse;
import com.parkzen.dto.response.ParkingAreaResponse;
import com.parkzen.dto.response.ParkingSlotResponse;

import java.util.List;

public interface BookingService {
    List<ParkingAreaResponse> getNearbyParkings(double lat, double lng, double radiusKm);
    ParkingAreaResponse getParkingById(Long parkingId);
    List<ParkingSlotResponse> getSlotsByParking(Long parkingId);
    BookingResponse bookSlot(BookingRequest request);
    BookingResponse getBookingTicket(Long bookingId);
    BookingResponse extendBooking(Long bookingId, ExtendBookingRequest request);
    List<BookingResponse> getUserBookingHistory();
}
