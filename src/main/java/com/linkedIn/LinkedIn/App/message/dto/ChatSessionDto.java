package com.linkedIn.LinkedIn.App.message.dto;

import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSessionDto {
    private Long id;
    private UserDto user1;
    private UserDto user2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
