package com.linkedIn.LinkedIn.App.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsResponse {

    private String message;
    private boolean success;
    private int totalNotifications;
    private List<NotificationDto> notifications;

}
