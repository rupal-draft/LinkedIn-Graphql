package com.linkedIn.LinkedIn.App.notification.dto;

import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private UserDto receiver;
}
