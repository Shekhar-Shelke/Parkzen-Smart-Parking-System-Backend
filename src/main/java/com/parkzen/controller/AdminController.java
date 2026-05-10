package com.parkzen.controller;

import com.parkzen.dto.request.AlertRequest;
import com.parkzen.dto.request.MessageRequest;
import com.parkzen.dto.response.*;
import com.parkzen.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched", adminService.getDashboard()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users fetched", adminService.getAllUsers()));
    }

    @GetMapping("/owners")
    public ResponseEntity<ApiResponse<List<OwnerResponse>>> getAllOwners() {
        return ResponseEntity.ok(ApiResponse.success("Owners fetched", adminService.getAllOwners()));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings() {
        return ResponseEntity.ok(ApiResponse.success("Bookings fetched", adminService.getAllBookings()));
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success("Payments fetched", adminService.getAllPayments()));
    }

    @GetMapping("/feedbacks")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getAllFeedbacks() {
        return ResponseEntity.ok(ApiResponse.success("Feedbacks fetched", adminService.getAllFeedbacks()));
    }

    @PutMapping("/owner/approve/{ownerId}")
    public ResponseEntity<ApiResponse<OwnerResponse>> approveOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success("Owner approved successfully", adminService.approveOwner(ownerId)));
    }

    @PutMapping("/owner/reject/{ownerId}")
    public ResponseEntity<ApiResponse<OwnerResponse>> rejectOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success("Owner rejected", adminService.rejectOwner(ownerId)));
    }

    @GetMapping("/complaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints() {
        return ResponseEntity.ok(ApiResponse.success("Complaints fetched", adminService.getAllComplaints()));
    }

    @GetMapping("/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getAllMessages() {
        return ResponseEntity.ok(ApiResponse.success("Messages fetched", adminService.getAllMessages()));
    }

    @PostMapping("/send-message-owner")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessageToOwner(@Valid @RequestBody MessageRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Message sent to owner", adminService.sendMessageToOwner(request)));
    }

    @PostMapping("/fire-alert")
    public ResponseEntity<ApiResponse<Void>> sendFireAlert(@Valid @RequestBody AlertRequest request) {
        adminService.sendFireAlert(request);
        return ResponseEntity.ok(ApiResponse.success("Fire alert broadcast to all users"));
    }

    @PostMapping("/parking-full-alert")
    public ResponseEntity<ApiResponse<Void>> sendParkingFullAlert(@Valid @RequestBody AlertRequest request) {
        adminService.sendParkingFullAlert(request);
        return ResponseEntity.ok(ApiResponse.success("Parking full alert broadcast to all users"));
    }
}
