package com.linkedIn.LinkedIn.App.message.dto.records;

import jakarta.validation.constraints.NotBlank;

public record CreateChatSessionInput(

        @NotBlank(message = "Receiver ID is required")
        Long receiverId
) {
}
