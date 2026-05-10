package com.parkzen.service.impl;

import com.parkzen.dto.request.SlotRequest;
import com.parkzen.dto.response.*;
import com.parkzen.entity.Owner;
import com.parkzen.entity.ParkingArea;
import com.parkzen.entity.ParkingSlot;
import com.parkzen.enums.SlotStatus;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.exception.UnauthorizedException;
import com.parkzen.repository.*;
import com.parkzen.service.OwnerService;
import com.parkzen.util.EntityMapper;
import com.parkzen.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerServiceImpl implements OwnerService {

    private final ParkingAreaRepository parkingAreaRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final FeedbackRepository feedbackRepository;
    private final SecurityUtil securityUtil;
    private final EntityMapper entityMapper;

    @Override
    public OwnerAnalyticsResponse getDashboard() {
        return getAnalytics();
    }

    @Override
    @Transactional
    public ParkingSlotResponse addSlot(SlotRequest request) {
        Owner owner = securityUtil.getCurrentOwner();
        ParkingArea parkingArea = parkingAreaRepository.findById(request.getParkingAreaId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking area not found"));

        if (!parkingArea.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You do not own this parking area");
        }

        ParkingSlot slot = ParkingSlot.builder()
                .slotNumber(request.getSlotNumber())
                .slotType(request.getSlotType())
                .chargingEnabled(request.getChargingEnabled())
                .availabilityStatus(SlotStatus.AVAILABLE)
                .parkingArea(parkingArea)
                .build();

        slot = parkingSlotRepository.save(slot);

        // Update total and available slots
        parkingArea.setTotalSlots(parkingArea.getTotalSlots() + 1);
        parkingArea.setAvailableSlots(parkingArea.getAvailableSlots() + 1);
        parkingAreaRepository.save(parkingArea);

        return entityMapper.toParkingSlotResponse(slot);
    }

    @Override
    @Transactional
    public ParkingSlotResponse updateSlot(Long slotId, SlotRequest request) {
        Owner owner = securityUtil.getCurrentOwner();
        ParkingSlot slot = getOwnerSlot(slotId, owner.getId());

        slot.setSlotNumber(request.getSlotNumber());
        slot.setSlotType(request.getSlotType());
        slot.setChargingEnabled(request.getChargingEnabled());

        slot = parkingSlotRepository.save(slot);
        return entityMapper.toParkingSlotResponse(slot);
    }

    @Override
    @Transactional
    public void deleteSlot(Long slotId) {
        Owner owner = securityUtil.getCurrentOwner();
        ParkingSlot slot = getOwnerSlot(slotId, owner.getId());

        ParkingArea parkingArea = slot.getParkingArea();
        parkingArea.setTotalSlots(parkingArea.getTotalSlots() - 1);
        if (slot.getAvailabilityStatus() == SlotStatus.AVAILABLE) {
            parkingArea.setAvailableSlots(parkingArea.getAvailableSlots() - 1);
        }
        parkingAreaRepository.save(parkingArea);

        parkingSlotRepository.delete(slot);
        log.info("Slot {} deleted by owner {}", slotId, owner.getId());
    }

    @Override
    @Transactional
    public ParkingSlotResponse updateSlotStatus(Long slotId, String status) {
        Owner owner = securityUtil.getCurrentOwner();
        ParkingSlot slot = getOwnerSlot(slotId, owner.getId());

        SlotStatus oldStatus = slot.getAvailabilityStatus();
        SlotStatus newStatus = SlotStatus.valueOf(status.toUpperCase());
        slot.setAvailabilityStatus(newStatus);

        // Update available slots count
        ParkingArea area = slot.getParkingArea();
        if (oldStatus == SlotStatus.AVAILABLE && newStatus != SlotStatus.AVAILABLE) {
            area.setAvailableSlots(Math.max(0, area.getAvailableSlots() - 1));
        } else if (oldStatus != SlotStatus.AVAILABLE && newStatus == SlotStatus.AVAILABLE) {
            area.setAvailableSlots(area.getAvailableSlots() + 1);
        }
        parkingAreaRepository.save(area);

        slot = parkingSlotRepository.save(slot);
        return entityMapper.toParkingSlotResponse(slot);
    }

    @Override
    public List<BookingResponse> getOwnerBookings() {
        Owner owner = securityUtil.getCurrentOwner();
        return bookingRepository.findByParkingSlotParkingAreaOwnerIdOrderByBookingTimeDesc(owner.getId())
                .stream()
                .map(entityMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getOwnerPayments() {
        Owner owner = securityUtil.getCurrentOwner();
        return paymentRepository.findByOwnerId(owner.getId())
                .stream()
                .map(entityMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OwnerAnalyticsResponse getAnalytics() {
        Owner owner = securityUtil.getCurrentOwner();

        long totalBookings = bookingRepository.countByParkingSlotParkingAreaOwnerId(owner.getId());
        Double totalEarnings = paymentRepository.sumEarningsByOwnerId(owner.getId());

        // Today's earnings
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        List<?> todayBookings = bookingRepository.findOwnerBookingsBetween(owner.getId(), startOfDay, endOfDay);
        double todayEarnings = todayBookings.stream()
                .mapToDouble(b -> ((com.parkzen.entity.Booking) b).getTotalAmount())
                .sum();

        // Monthly earnings
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        List<?> monthlyBookings = bookingRepository.findOwnerBookingsBetween(owner.getId(), startOfMonth, endOfMonth);
        double monthlyEarnings = monthlyBookings.stream()
                .mapToDouble(b -> ((com.parkzen.entity.Booking) b).getTotalAmount())
                .sum();

        // Slot usage
        List<ParkingArea> areas = parkingAreaRepository.findByOwnerId(owner.getId());
        int totalSlots = areas.stream().mapToInt(ParkingArea::getTotalSlots).sum();
        int availableSlots = areas.stream().mapToInt(ParkingArea::getAvailableSlots).sum();
        int bookedSlots = totalSlots - availableSlots;
        double slotUsagePercentage = totalSlots > 0 ? (bookedSlots * 100.0 / totalSlots) : 0;

        // Reviews
        List<com.parkzen.entity.Feedback> feedbacks = feedbackRepository.findByOwnerId(owner.getId());
        double avgRating = feedbacks.stream()
                .mapToInt(com.parkzen.entity.Feedback::getRating)
                .average()
                .orElse(0.0);

        return OwnerAnalyticsResponse.builder()
                .totalBookings(totalBookings)
                .totalEarnings(totalEarnings != null ? totalEarnings : 0.0)
                .todayEarnings(todayEarnings)
                .monthlyEarnings(monthlyEarnings)
                .slotUsagePercentage(Math.round(slotUsagePercentage * 100.0) / 100.0)
                .totalSlots(totalSlots)
                .availableSlots(availableSlots)
                .bookedSlots(bookedSlots)
                .totalReviews((long) feedbacks.size())
                .averageRating(Math.round(avgRating * 10.0) / 10.0)
                .build();
    }

    @Override
    public List<FeedbackResponse> getOwnerReviews() {
        Owner owner = securityUtil.getCurrentOwner();
        return feedbackRepository.findByOwnerId(owner.getId())
                .stream()
                .map(entityMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    private ParkingSlot getOwnerSlot(Long slotId, Long ownerId) {
        ParkingSlot slot = parkingSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + slotId));
        if (!slot.getParkingArea().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedException("You do not have access to this slot");
        }
        return slot;
    }
}
