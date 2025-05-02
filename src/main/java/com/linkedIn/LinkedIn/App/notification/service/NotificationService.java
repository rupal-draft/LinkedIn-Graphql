package com.linkedIn.LinkedIn.App.notification.service;

import com.linkedIn.LinkedIn.App.notification.dto.NotificationDto;
import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.user.entity.User;

import java.util.List;

public interface NotificationService {

    void createNotification(String title, String message, User receiver, NotificationType type);

    List<NotificationDto> getMyNotifications();
}
