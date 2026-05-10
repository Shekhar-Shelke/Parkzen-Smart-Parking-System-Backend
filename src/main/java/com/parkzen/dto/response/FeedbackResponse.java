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
public class FeedbackResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long ownerId;
    private String ownerName;
}
