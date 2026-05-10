package com.parkzen.service;

import com.parkzen.dto.request.PaymentVerifyRequest;
import com.parkzen.dto.response.PaymentResponse;
import com.parkzen.dto.response.RazorpayOrderResponse;

public interface PaymentService {
    RazorpayOrderResponse createOrder(Long bookingId);
    PaymentResponse verifyPayment(PaymentVerifyRequest request);
}
