package com.parkzen.controller;

import com.parkzen.dto.request.*;
import com.parkzen.dto.response.*;
import com.parkzen.service.BookingService;
import com.parkzen.service.PaymentService;
import com.parkzen.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserController {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final UserService userService;

    @GetMapping("/parkings/nearby")
    public ResponseEntity<ApiResponse<List<ParkingAreaResponse>>> getNearbyParkings(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "10.0") double radius) {
        return ResponseEntity.ok(ApiResponse.success("Nearby parkings fetched",
                bookingService.getNearbyParkings(lat, lng, radius)));
    }

    @GetMapping("/parking/{parkingId}")
    public ResponseEntity<ApiResponse<ParkingAreaResponse>> getParkingById(@PathVariable Long parkingId) {
        return ResponseEntity.ok(ApiResponse.success("Parking area fetched",
                bookingService.getParkingById(parkingId)));
    }

    @GetMapping("/slots/{parkingId}")
    public ResponseEntity<ApiResponse<List<ParkingSlotResponse>>> getSlots(@PathVariable Long parkingId) {
        return ResponseEntity.ok(ApiResponse.success("Slots fetched",
                bookingService.getSlotsByParking(parkingId)));
    }

    @PostMapping("/book-slot")
    public ResponseEntity<ApiResponse<BookingResponse>> bookSlot(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Slot booked. Proceed to payment.", bookingService.bookSlot(request)));
    }

    @PostMapping("/payment/create-order")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createPaymentOrder(@RequestParam Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Razorpay order created",
                paymentService.createOrder(bookingId)));
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment verified successfully",
                paymentService.verifyPayment(request)));
    }

    @GetMapping("/ticket/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getTicket(@PathVariable Long bookingId) {
        return ResponseEntity.ok(ApiResponse.success("Booking ticket fetched",
                bookingService.getBookingTicket(bookingId)));
    }

    @PutMapping("/booking/extend/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> extendBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody ExtendBookingRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Booking extended successfully",
                bookingService.extendBooking(bookingId, request)));
    }

    @GetMapping("/bookings/history")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookingHistory() {
        return ResponseEntity.ok(ApiResponse.success("Booking history fetched",
                bookingService.getUserBookingHistory()));
    }

    @PostMapping("/review/add")
    public ResponseEntity<ApiResponse<FeedbackResponse>> addReview(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", userService.addReview(request)));
    }

    @PostMapping("/complaint/add")
    public ResponseEntity<ApiResponse<ComplaintResponse>> addComplaint(@Valid @RequestBody ComplaintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted successfully", userService.addComplaint(request)));
    }

    @PostMapping("/contact-admin")
    public ResponseEntity<ApiResponse<MessageResponse>> contactAdmin(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent to admin", userService.contactAdmin(request)));
    }
}
