package com.linkedIn.LinkedIn.App.message.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
}, indexes = {
        @Index(name = "idx_user1_id", columnList = "user1_id"),
        @Index(name = "idx_user2_id", columnList = "user2_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;
}

