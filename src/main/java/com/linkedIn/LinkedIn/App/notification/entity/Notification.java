package com.linkedIn.LinkedIn.App.notification.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_receiver", columnList = "receiver_id"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
}

