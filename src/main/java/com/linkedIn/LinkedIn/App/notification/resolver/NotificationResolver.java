package com.linkedIn.LinkedIn.App.notification.resolver;

import com.linkedIn.LinkedIn.App.notification.dto.NotificationDto;
import com.linkedIn.LinkedIn.App.notification.dto.NotificationsResponse;
import com.linkedIn.LinkedIn.App.notification.service.NotificationService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class NotificationResolver {

    private final NotificationService notificationService;

    public NotificationResolver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @QueryMapping
    public NotificationsResponse getMyNotifications() {
        List<NotificationDto> notifications = notificationService.getMyNotifications();
        return new NotificationsResponse("Notifications fetched successfully", true, notifications.size(), notifications);
    }
}
