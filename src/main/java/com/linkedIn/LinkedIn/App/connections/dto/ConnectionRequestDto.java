package com.linkedIn.LinkedIn.App.connections.dto;

import com.linkedIn.LinkedIn.App.connections.entity.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequestDto {
    private Long id;

    private Long senderId;
    private String senderName;
    private String senderEmail;

    private Long receiverId;
    private String receiverName;
    private String receiverEmail;

    private RequestStatus status;

    private LocalDateTime createdAt;
}

