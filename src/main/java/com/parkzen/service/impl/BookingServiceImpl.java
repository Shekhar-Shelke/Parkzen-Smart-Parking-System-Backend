package com.parkzen.service.impl;

import com.parkzen.dto.request.BookingRequest;
import com.parkzen.dto.request.ExtendBookingRequest;
import com.parkzen.dto.response.BookingResponse;
import com.parkzen.dto.response.ParkingAreaResponse;
import com.parkzen.dto.response.ParkingSlotResponse;
import com.parkzen.entity.*;
import com.parkzen.enums.BookingStatus;
import com.parkzen.enums.NotificationType;
import com.parkzen.enums.SlotStatus;
import com.parkzen.exception.BookingConflictException;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.exception.UnauthorizedException;
import com.parkzen.repository.*;
import com.parkzen.service.BookingService;
import com.parkzen.util.EntityMapper;
import com.parkzen.util.QRCodeUtil;
import com.parkzen.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ParkingAreaRepository parkingAreaRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtil securityUtil;
    private final EntityMapper entityMapper;
    private final QRCodeUtil qrCodeUtil;

    @Override
    public List<ParkingAreaResponse> getNearbyParkings(double lat, double lng, double radiusKm) {
        return parkingAreaRepository.findNearbyParkingAreas(lat, lng, radiusKm)
                .stream()
                .map(area -> {
                    ParkingAreaResponse response = entityMapper.toParkingAreaResponse(area);
                    response.setDistanceKm(calculateDistance(lat, lng, area.getLatitude(), area.getLongitude()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ParkingAreaResponse getParkingById(Long parkingId) {
        ParkingArea area = parkingAreaRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found with ID: " + parkingId));
        return entityMapper.toParkingAreaResponse(area);
    }

    @Override
    public List<ParkingSlotResponse> getSlotsByParking(Long parkingId) {
        parkingAreaRepository.findById(parkingId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found with ID: " + parkingId));
        return parkingSlotRepository.findByParkingAreaId(parkingId)
                .stream()
                .map(entityMapper::toParkingSlotResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse bookSlot(BookingRequest request) {
        User user = securityUtil.getCurrentUser();

        ParkingArea parkingArea = parkingAreaRepository.findById(request.getParkingAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found"));

        if (!"ACTIVE".equals(parkingArea.getStatus())) {
            throw new BookingConflictException("This parking area is currently not active");
        }

        if (parkingArea.getAvailableSlots() <= 0) {
            throw new BookingConflictException("No available slots in this parking area");
        }

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusHours(request.getDurationHours());

        // Find available slots for the requested time window
        List<ParkingSlot> availableSlots = parkingSlotRepository.findAvailableSlots(
                request.getParkingAreaId(), startTime, endTime);

        if (availableSlots.isEmpty()) {
            throw new BookingConflictException("No available slots for the requested time period");
        }

        // Choose slot based on charging preference
        ParkingSlot selectedSlot;
        if (Boolean.TRUE.equals(request.getRequestCharging())) {
            selectedSlot = availableSlots.stream()
                    .filter(ParkingSlot::getChargingEnabled)
                    .findFirst()
                    .orElseThrow(() -> new BookingConflictException("No charging slots available for the requested time"));
        } else {
            selectedSlot = availableSlots.get(0);
        }

        // Check for booking conflicts on selected slot
        List<Booking> conflicts = bookingRepository.findConflictingBookings(selectedSlot.getId(), startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Slot " + selectedSlot.getSlotNumber() + " is already booked for this time");
        }

        // Calculate total amount
        double durationHours = request.getDurationHours();
        double totalAmount = parkingArea.getPricePerHour() * durationHours;
        if (selectedSlot.getChargingEnabled() && parkingArea.getChargingAvailable()) {
            totalAmount += parkingArea.getChargingPricePerHour() * durationHours;
        }

        // Create booking
        Booking booking = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .durationHours(durationHours)
                .totalAmount(totalAmount)
                .bookingStatus(BookingStatus.PENDING)
                .user(user)
                .parkingSlot(selectedSlot)
                .extendable(true)
                .build();

        booking = bookingRepository.save(booking);

        // Generate QR code
        String qrContent = qrCodeUtil.buildBookingQRContent(
                booking.getId(), user.getName(), selectedSlot.getSlotNumber(),
                parkingArea.getName(), startTime.toString(), endTime.toString());
        booking.setQrCode(qrCodeUtil.generateQRCodeBase64(qrContent));

        // Update available slots
        parkingArea.setAvailableSlots(parkingArea.getAvailableSlots() - 1);
        parkingAreaRepository.save(parkingArea);

        booking = bookingRepository.save(booking);

        // Send notification
        sendNotification(user, "Booking Created",
                "Your slot " + selectedSlot.getSlotNumber() + " at " + parkingArea.getName() + " has been reserved. Complete payment to confirm.",
                NotificationType.BOOKING_CONFIRMED);

        log.info("Booking created: {} for user: {}", booking.getId(), user.getEmail());
        return entityMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse getBookingTicket(Long bookingId) {
        User user = securityUtil.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to view this booking");
        }

        return entityMapper.toBookingResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse extendBooking(Long bookingId, ExtendBookingRequest request) {
        User user = securityUtil.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to extend this booking");
        }

        if (!booking.getExtendable()) {
            throw new BookingConflictException("This booking cannot be extended");
        }

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED && booking.getBookingStatus() != BookingStatus.ACTIVE) {
            throw new BookingConflictException("Only confirmed or active bookings can be extended");
        }

        LocalDateTime newEndTime = booking.getEndTime().plusHours(request.getAdditionalHours());

        // Check for conflicts after extension
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                booking.getParkingSlot().getId(), booking.getEndTime(), newEndTime);

        conflicts = conflicts.stream()
                .filter(b -> !b.getId().equals(bookingId))
                .collect(Collectors.toList());

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Cannot extend booking - slot is reserved by another user for the extended period");
        }

        ParkingArea area = booking.getParkingSlot().getParkingArea();
        double additionalAmount = area.getPricePerHour() * request.getAdditionalHours();
        if (booking.getParkingSlot().getChargingEnabled() && area.getChargingAvailable()) {
            additionalAmount += area.getChargingPricePerHour() * request.getAdditionalHours();
        }

        booking.setEndTime(newEndTime);
        booking.setDurationHours(booking.getDurationHours() + request.getAdditionalHours());
        booking.setTotalAmount(booking.getTotalAmount() + additionalAmount);
        booking.setBookingStatus(BookingStatus.EXTENDED);

        booking = bookingRepository.save(booking);

        sendNotification(user, "Booking Extended",
                "Your booking at " + area.getName() + " has been extended until " + newEndTime,
                NotificationType.BOOKING_EXTENDED);

        return entityMapper.toBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getUserBookingHistory() {
        User user = securityUtil.getCurrentUser();
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(user.getId())
                .stream()
                .map(entityMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }

    private void sendNotification(User user, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .notificationType(type)
                .user(user)
                .build();
        notificationRepository.save(notification);
    }
}
