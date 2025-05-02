package com.linkedIn.LinkedIn.App.notification.repository;

import com.linkedIn.LinkedIn.App.notification.entity.Notification;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiver(User Receiver);
}
