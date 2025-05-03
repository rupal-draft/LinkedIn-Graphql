package com.linkedIn.LinkedIn.App.message.repository;

import com.linkedIn.LinkedIn.App.message.entity.ChatSession;
import com.linkedIn.LinkedIn.App.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySessionOrderBySentAtAsc(ChatSession session);


    @Modifying
    @Query("UPDATE Message m SET m.seen = true WHERE m.session = :session AND m.sender.id <> :currentUserId AND m.seen = false")
    int markMessagesAsSeen(@Param("session") ChatSession session, @Param("currentUserId") Long currentUserId);

}
