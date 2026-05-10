package com.parkzen.service;

import com.parkzen.dto.request.ComplaintRequest;
import com.parkzen.dto.request.FeedbackRequest;
import com.parkzen.dto.request.MessageRequest;
import com.parkzen.dto.response.ComplaintResponse;
import com.parkzen.dto.response.FeedbackResponse;
import com.parkzen.dto.response.MessageResponse;

public interface UserService {
    FeedbackResponse addReview(FeedbackRequest request);
    ComplaintResponse addComplaint(ComplaintRequest request);
    MessageResponse contactAdmin(MessageRequest request);
}
