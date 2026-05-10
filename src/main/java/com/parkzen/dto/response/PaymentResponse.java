package com.parkzen.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private Double amount;
    private String paymentStatus;
    private LocalDateTime paymentTime;
    private Long bookingId;
}
