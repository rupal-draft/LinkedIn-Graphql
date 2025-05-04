package com.linkedIn.LinkedIn.App.message.service;

import com.linkedIn.LinkedIn.App.message.dto.ChatSessionDto;
import com.linkedIn.LinkedIn.App.message.dto.MessageDto;
import com.linkedIn.LinkedIn.App.message.dto.records.SendMessageInput;
import reactor.core.publisher.Flux;

import java.util.List;

public interface MessageService {

    ChatSessionDto createOrGetChatSession(Long receiverId);

    List<ChatSessionDto> getMyChatSessions();

    MessageDto sendMessage(SendMessageInput input);

    List<MessageDto> getMessagesBySession(Long sessionId);

    String markMessagesAsSeen(Long sessionId);
}

