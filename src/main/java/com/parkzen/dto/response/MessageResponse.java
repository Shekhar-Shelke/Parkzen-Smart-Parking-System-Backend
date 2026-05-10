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
public class MessageResponse {
    private Long id;
    private String senderRole;
    private String receiverRole;
    private String message;
    private Long senderId;
    private Long receiverId;
    private LocalDateTime sentAt;
}
