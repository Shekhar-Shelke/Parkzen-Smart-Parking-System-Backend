package com.parkzen.service.impl;

import com.parkzen.dto.request.AlertRequest;
import com.parkzen.dto.request.MessageRequest;
import com.parkzen.dto.response.*;
import com.parkzen.entity.*;
import com.parkzen.enums.BookingStatus;
import com.parkzen.enums.ComplaintStatus;
import com.parkzen.enums.NotificationType;
import com.parkzen.enums.PaymentStatus;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.repository.*;
import com.parkzen.service.AdminService;
import com.parkzen.util.EntityMapper;
import com.parkzen.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final SecurityUtil securityUtil;
    private final EntityMapper entityMapper;

    @Override
    public AdminDashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalOwners = ownerRepository.count();
        long totalBookings = bookingRepository.count();
        Double totalRevenue = paymentRepository.sumTotalRevenue();
        long activeBookings = bookingRepository.countByBookingStatus(BookingStatus.CONFIRMED)
                + bookingRepository.countByBookingStatus(BookingStatus.ACTIVE);
        long pendingComplaints = complaintRepository.countByComplaintStatus(ComplaintStatus.OPEN);
        long totalTransactions = paymentRepository.count();
        long pendingOwnerApprovals = ownerRepository.findAll()
                .stream().filter(o -> !o.getApproved()).count();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalOwners(totalOwners)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .activeBookings(activeBookings)
                .pendingComplaints(pendingComplaints)
                .totalTransactions(totalTransactions)
                .pendingOwnerApprovals(pendingOwnerApprovals)
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(entityMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OwnerResponse> getAllOwners() {
        return ownerRepository.findAll().stream()
                .map(entityMapper::toOwnerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(entityMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(entityMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(entityMapper::toFeedbackResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OwnerResponse approveOwner(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));
        owner.setApproved(true);
        owner = ownerRepository.save(owner);
        log.info("Owner {} approved by admin", ownerId);
        return entityMapper.toOwnerResponse(owner);
    }

    @Override
    @Transactional
    public OwnerResponse rejectOwner(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));
        owner.setApproved(false);
        owner = ownerRepository.save(owner);
        log.info("Owner {} rejected by admin", ownerId);
        return entityMapper.toOwnerResponse(owner);
    }

    @Override
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll().stream()
                .map(entityMapper::toComplaintResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponse> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(entityMapper::toMessageResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponse sendMessageToOwner(MessageRequest request) {
        Admin admin = securityUtil.getCurrentAdmin();

        Message message = Message.builder()
                .senderRole("ROLE_ADMIN")
                .receiverRole("ROLE_OWNER")
                .message(request.getMessage())
                .senderId(admin.getId())
                .receiverId(request.getReceiverId())
                .build();

        message = messageRepository.save(message);
        return entityMapper.toMessageResponse(message);
    }

    @Override
    @Transactional
    public void sendFireAlert(AlertRequest request) {
        List<User> allUsers = userRepository.findAll();
        String title = request.getTitle() != null ? request.getTitle() : "🔥 FIRE ALERT";
        String message = request.getMessage();

        for (User user : allUsers) {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.FIRE_ALERT)
                    .user(user)
                    .build();
            notificationRepository.save(notification);
        }
        log.info("Fire alert sent to {} users", allUsers.size());
    }

    @Override
    @Transactional
    public void sendParkingFullAlert(AlertRequest request) {
        List<User> allUsers = userRepository.findAll();
        String title = request.getTitle() != null ? request.getTitle() : "🚗 Parking Full Alert";
        String message = request.getMessage();

        for (User user : allUsers) {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .notificationType(NotificationType.PARKING_FULL)
                    .user(user)
                    .build();
            notificationRepository.save(notification);
        }
        log.info("Parking full alert sent to {} users", allUsers.size());
    }
}
