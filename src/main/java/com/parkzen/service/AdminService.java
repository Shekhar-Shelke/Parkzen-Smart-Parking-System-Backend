package com.parkzen.service;

import com.parkzen.dto.request.AlertRequest;
import com.parkzen.dto.request.MessageRequest;
import com.parkzen.dto.response.*;

import java.util.List;

public interface AdminService {
    AdminDashboardResponse getDashboard();
    List<UserResponse> getAllUsers();
    List<OwnerResponse> getAllOwners();
    List<BookingResponse> getAllBookings();
    List<PaymentResponse> getAllPayments();
    List<FeedbackResponse> getAllFeedbacks();
    OwnerResponse approveOwner(Long ownerId);
    OwnerResponse rejectOwner(Long ownerId);
    List<ComplaintResponse> getAllComplaints();
    List<MessageResponse> getAllMessages();
    MessageResponse sendMessageToOwner(MessageRequest request);
    void sendFireAlert(AlertRequest request);
    void sendParkingFullAlert(AlertRequest request);
}
