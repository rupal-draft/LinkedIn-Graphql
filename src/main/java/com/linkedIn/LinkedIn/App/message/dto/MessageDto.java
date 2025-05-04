package com.linkedIn.LinkedIn.App.message.dto;

import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private String content;
    private LocalDateTime sentAt;
    private boolean seen;
    private UserDto sender;
    private UserDto receiver;
}
