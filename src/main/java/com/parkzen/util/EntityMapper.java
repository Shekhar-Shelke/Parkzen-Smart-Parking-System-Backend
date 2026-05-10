package com.parkzen.util;

import com.parkzen.dto.response.*;
import com.parkzen.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .vehicleNumber(user.getVehicleNumber())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public OwnerResponse toOwnerResponse(Owner owner) {
        return OwnerResponse.builder()
                .id(owner.getId())
                .name(owner.getName())
                .parkingAreaName(owner.getParkingAreaName())
                .address(owner.getAddress())
                .latitude(owner.getLatitude())
                .longitude(owner.getLongitude())
                .email(owner.getEmail())
                .approved(owner.getApproved())
                .role(owner.getRole().name())
                .createdAt(owner.getCreatedAt())
                .build();
    }

    public ParkingAreaResponse toParkingAreaResponse(ParkingArea area) {
        return ParkingAreaResponse.builder()
                .id(area.getId())
                .name(area.getName())
                .address(area.getAddress())
                .latitude(area.getLatitude())
                .longitude(area.getLongitude())
                .totalSlots(area.getTotalSlots())
                .availableSlots(area.getAvailableSlots())
                .pricePerHour(area.getPricePerHour())
                .chargingAvailable(area.getChargingAvailable())
                .chargingPricePerHour(area.getChargingPricePerHour())
                .status(area.getStatus())
                .ownerId(area.getOwner().getId())
                .ownerName(area.getOwner().getName())
                .build();
    }

    public ParkingSlotResponse toParkingSlotResponse(ParkingSlot slot) {
        return ParkingSlotResponse.builder()
                .id(slot.getId())
                .slotNumber(slot.getSlotNumber())
                .slotType(slot.getSlotType())
                .availabilityStatus(slot.getAvailabilityStatus().name())
                .chargingEnabled(slot.getChargingEnabled())
                .parkingAreaId(slot.getParkingArea().getId())
                .parkingAreaName(slot.getParkingArea().getName())
                .build();
    }

    public BookingResponse toBookingResponse(Booking booking) {
        BookingResponse.BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .bookingTime(booking.getBookingTime())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .durationHours(booking.getDurationHours())
                .totalAmount(booking.getTotalAmount())
                .bookingStatus(booking.getBookingStatus().name())
                .qrCode(booking.getQrCode())
                .extendable(booking.getExtendable())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .slotId(booking.getParkingSlot().getId())
                .slotNumber(booking.getParkingSlot().getSlotNumber())
                .parkingAreaId(booking.getParkingSlot().getParkingArea().getId())
                .parkingAreaName(booking.getParkingSlot().getParkingArea().getName())
                .parkingAreaAddress(booking.getParkingSlot().getParkingArea().getAddress());

        if (booking.getPayment() != null) {
            builder.payment(toPaymentResponse(booking.getPayment()));
        }

        return builder.build();
    }

    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .amount(payment.getAmount())
                .paymentStatus(payment.getPaymentStatus().name())
                .paymentTime(payment.getPaymentTime())
                .bookingId(payment.getBooking().getId())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .userId(feedback.getUser().getId())
                .userName(feedback.getUser().getName())
                .ownerId(feedback.getOwner().getId())
                .ownerName(feedback.getOwner().getName())
                .build();
    }

    public ComplaintResponse toComplaintResponse(Complaint complaint) {
        ComplaintResponse.ComplaintResponseBuilder builder = ComplaintResponse.builder()
                .id(complaint.getId())
                .subject(complaint.getSubject())
                .message(complaint.getMessage())
                .complaintStatus(complaint.getComplaintStatus().name())
                .createdAt(complaint.getCreatedAt())
                .userId(complaint.getUser().getId())
                .userName(complaint.getUser().getName());

        if (complaint.getOwner() != null) {
            builder.ownerId(complaint.getOwner().getId())
                   .ownerName(complaint.getOwner().getName());
        }

        return builder.build();
    }

    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderRole(message.getSenderRole())
                .receiverRole(message.getReceiverRole())
                .message(message.getMessage())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .sentAt(message.getSentAt())
                .build();
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType().name())
                .createdAt(notification.getCreatedAt())
                .userId(notification.getUser().getId())
                .build();
    }
}
