package com.linkedIn.LinkedIn.App.message.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageInput(

        @NotNull(message = "Session ID is required")
        Long sessionId,

        @NotBlank(message = "Content is required")
        @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
        String content
) {
}
