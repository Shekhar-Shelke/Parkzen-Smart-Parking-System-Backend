package com.parkzen.service.impl;

import com.parkzen.dto.request.ComplaintRequest;
import com.parkzen.dto.request.FeedbackRequest;
import com.parkzen.dto.request.MessageRequest;
import com.parkzen.dto.response.ComplaintResponse;
import com.parkzen.dto.response.FeedbackResponse;
import com.parkzen.dto.response.MessageResponse;
import com.parkzen.entity.*;
import com.parkzen.exception.ResourceNotFoundException;
import com.parkzen.repository.*;
import com.parkzen.service.UserService;
import com.parkzen.util.EntityMapper;
import com.parkzen.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final FeedbackRepository feedbackRepository;
    private final ComplaintRepository complaintRepository;
    private final MessageRepository messageRepository;
    private final OwnerRepository ownerRepository;
    private final SecurityUtil securityUtil;
    private final EntityMapper entityMapper;

    @Override
    @Transactional
    public FeedbackResponse addReview(FeedbackRequest request) {
        User user = securityUtil.getCurrentUser();
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));

        Feedback feedback = Feedback.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .owner(owner)
                .build();

        feedback = feedbackRepository.save(feedback);
        log.info("Review added by user {} for owner {}", user.getId(), owner.getId());
        return entityMapper.toFeedbackResponse(feedback);
    }

    @Override
    @Transactional
    public ComplaintResponse addComplaint(ComplaintRequest request) {
        User user = securityUtil.getCurrentUser();
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + request.getOwnerId()));

        Complaint complaint = Complaint.builder()
                .subject(request.getSubject())
                .message(request.getMessage())
                .user(user)
                .owner(owner)
                .build();

        complaint = complaintRepository.save(complaint);
        log.info("Complaint added by user {} against owner {}", user.getId(), owner.getId());
        return entityMapper.toComplaintResponse(complaint);
    }

    @Override
    @Transactional
    public MessageResponse contactAdmin(MessageRequest request) {
        User user = securityUtil.getCurrentUser();

        Message message = Message.builder()
                .senderRole("ROLE_USER")
                .receiverRole("ROLE_ADMIN")
                .message(request.getMessage())
                .senderId(user.getId())
                .receiverId(request.getReceiverId())
                .build();

        message = messageRepository.save(message);
        return entityMapper.toMessageResponse(message);
    }
}
