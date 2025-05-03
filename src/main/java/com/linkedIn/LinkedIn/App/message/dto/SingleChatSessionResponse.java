package com.linkedIn.LinkedIn.App.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleChatSessionResponse {

    private String message;
    private boolean success;
    private ChatSessionDto chatSession;
}
