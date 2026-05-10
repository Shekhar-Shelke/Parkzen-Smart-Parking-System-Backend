package com.parkzen.controller;

import com.parkzen.dto.request.SlotRequest;
import com.parkzen.dto.response.*;
import com.parkzen.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<OwnerAnalyticsResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", ownerService.getDashboard()));
    }

    @PostMapping("/slot/add")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> addSlot(@Valid @RequestBody SlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Slot added successfully", ownerService.addSlot(request)));
    }

    @PutMapping("/slot/update/{slotId}")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> updateSlot(
            @PathVariable Long slotId, @Valid @RequestBody SlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Slot updated successfully", ownerService.updateSlot(slotId, request)));
    }

    @DeleteMapping("/slot/delete/{slotId}")
    public ResponseEntity<ApiResponse<Void>> deleteSlot(@PathVariable Long slotId) {
        ownerService.deleteSlot(slotId);
        return ResponseEntity.ok(ApiResponse.success("Slot deleted successfully"));
    }

    @PutMapping("/slot/status/{slotId}")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> updateSlotStatus(
            @PathVariable Long slotId, @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Slot status updated",
                ownerService.updateSlotStatus(slotId, status)));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getBookings() {
        return ResponseEntity.ok(ApiResponse.success("Bookings fetched", ownerService.getOwnerBookings()));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPayments() {
        return ResponseEntity.ok(ApiResponse.success("Payments fetched", ownerService.getOwnerPayments()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<OwnerAnalyticsResponse>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success("Analytics fetched", ownerService.getAnalytics()));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getReviews() {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", ownerService.getOwnerReviews()));
    }
}
