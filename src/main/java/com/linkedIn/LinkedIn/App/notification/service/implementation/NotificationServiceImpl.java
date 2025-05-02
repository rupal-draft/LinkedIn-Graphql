package com.linkedIn.LinkedIn.App.notification.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.notification.dto.NotificationDto;
import com.linkedIn.LinkedIn.App.notification.entity.Notification;
import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.notification.repository.NotificationRepository;
import com.linkedIn.LinkedIn.App.notification.service.NotificationService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;

    @Override
    @Async
    @Transactional
    @CacheEvict(value = "notifications", allEntries = true)
    public void createNotification(String title, String message, User receiver, NotificationType type) {
        log.info("Creating notification for user ID: {}, Name: {}", receiver.getId(), receiver.getName());

        if (receiver == null || receiver.getId() == null) {
            log.error("Notification receiver is null or has no ID");
            throw new IllegalArgumentException("Receiver must be a valid user");
        }

        try {
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .receiver(receiver)
                    .type(type)
                    .build();

            notificationRepository.save(notification);
            log.info("Notification successfully saved for user ID: {}", receiver.getId());

        } catch (Exception ex) {
            log.error("Failed to create notification for user ID: {}: {}", receiver.getId(), ex.getMessage(), ex);
            throw new RuntimeException("Unable to create notification at this time. Please try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "notifications",
            key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()"
    )
    public List<NotificationDto> getMyNotifications() {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching notifications for user ID: {}", currentUser.getId());

        try {
            List<Notification> notifications = notificationRepository.findByReceiver(currentUser);

            if (notifications.isEmpty()) {
                log.warn("No notifications found for user ID: {}", currentUser.getId());
                return Collections.emptyList();
            }

            List<NotificationDto> notificationDtos = notifications.stream()
                    .map(notification -> {
                        try {
                            return modelMapper.map(notification, NotificationDto.class);
                        } catch (MappingException e) {
                            log.error("Error mapping Notification ID {}: {}", notification.getId(), e.getLocalizedMessage(), e);
                            throw new RuntimeException("Mapping failed for a notification", e);
                        }
                    })
                    .toList();

            log.info("Successfully fetched {} notifications for user ID: {}", notificationDtos.size(), currentUser.getId());
            return notificationDtos;

        } catch (Exception ex) {
            log.error("Failed to fetch notifications for user ID {}: {}", currentUser.getId(), ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch notifications at this time. Please try again later.");
        }
    }

}
