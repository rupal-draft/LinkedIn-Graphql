package com.linkedIn.LinkedIn.App.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionsResponse {
    private String message;
    private boolean success;
    private int totalSessions;
    private List<ChatSessionDto> sessions;
}

