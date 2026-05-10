package com.parkzen.service.impl;

import com.parkzen.dto.request.PaymentVerifyRequest;
import com.parkzen.dto.response.PaymentResponse;
import com.parkzen.dto.response.RazorpayOrderResponse;
import com.parkzen.entity.Booking;
import com.parkzen.entity.Notification;
import com.parkzen.entity.Payment;
import com.parkzen.entity.User;
import com.parkzen.enums.BookingStatus;
import com.parkzen.enums.NotificationType;
import com.parkzen.enums.PaymentStatus;
import com.parkzen.exception.PaymentException;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.exception.UnauthorizedException;
import com.parkzen.repository.BookingRepository;
import com.parkzen.repository.NotificationRepository;
import com.parkzen.repository.PaymentRepository;
import com.parkzen.service.PaymentService;
import com.parkzen.util.EntityMapper;
import com.parkzen.util.SecurityUtil;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtil securityUtil;
    private final EntityMapper entityMapper;
    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Override
    @Transactional
    public RazorpayOrderResponse createOrder(Long bookingId) {
        User user = securityUtil.getCurrentUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to pay for this booking");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new PaymentException("Payment already processed for this booking");
        }

        try {
            JSONObject orderRequest = new JSONObject();
            long amountInPaise = Math.round(booking.getTotalAmount() * 100);
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "booking_" + bookingId);

            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = razorpayOrder.get("id");

            Payment payment = Payment.builder()
                    .razorpayOrderId(razorpayOrderId)
                    .amount(booking.getTotalAmount())
                    .paymentStatus(PaymentStatus.PENDING)
                    .booking(booking)
                    .owner(booking.getParkingSlot().getParkingArea().getOwner())
                    .build();

            paymentRepository.save(payment);

            log.info("Razorpay order created: {} for booking: {}", razorpayOrderId, bookingId);

            return RazorpayOrderResponse.builder()
                    .razorpayOrderId(razorpayOrderId)
                    .bookingId(bookingId)
                    .amount(booking.getTotalAmount())
                    .currency("INR")
                    .razorpayKey(razorpayKey)
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay order creation failed: {}", e.getMessage());
            throw new PaymentException("Failed to create payment order: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) {
        User user = securityUtil.getCurrentUser();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to verify this payment");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for order: " + request.getRazorpayOrderId()));

        boolean isValid = verifyRazorpaySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature());

        if (!isValid) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentException("Payment verification failed - invalid signature");
        }

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Notification notification = Notification.builder()
                .title("Payment Successful")
                .message("Payment of ₹" + booking.getTotalAmount() + " confirmed for booking at "
                        + booking.getParkingSlot().getParkingArea().getName())
                .notificationType(NotificationType.PAYMENT_SUCCESS)
                .user(user)
                .build();
        notificationRepository.save(notification);

        log.info("Payment verified for booking: {}", booking.getId());
        return entityMapper.toPaymentResponse(payment);
    }

    private boolean verifyRazorpaySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpaySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString().equals(signature);
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}
