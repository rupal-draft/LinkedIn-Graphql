package com.linkedIn.LinkedIn.App.message.repository;

import com.linkedIn.LinkedIn.App.message.entity.ChatSession;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    @Query("SELECT cs FROM ChatSession cs WHERE " +
            "(cs.user1 = :user1 AND cs.user2 = :user2) OR " +
            "(cs.user1 = :user2 AND cs.user2 = :user1)")
    Optional<ChatSession> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT cs FROM ChatSession cs WHERE cs.user1 = :user OR cs.user2 = :user")
    List<ChatSession> findAllByUser(@Param("user") User user);
}
