package com.linkedIn.LinkedIn.App.message.repository;

import com.linkedIn.LinkedIn.App.message.entity.ChatSession;
import com.linkedIn.LinkedIn.App.message.entity.Message;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySessionOrderBySentAtAsc(ChatSession session);

    @Query("SELECT m FROM Message m WHERE m.session = :session AND m.seen = false AND m.sender <> :currentUser")
    List<Message> findUnseenMessagesForUser(@Param("session") ChatSession session, @Param("currentUser") User currentUser);
}
