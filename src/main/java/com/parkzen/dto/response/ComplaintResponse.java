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
public class ComplaintResponse {
    private Long id;
    private String subject;
    private String message;
    private String complaintStatus;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long ownerId;
    private String ownerName;
}
