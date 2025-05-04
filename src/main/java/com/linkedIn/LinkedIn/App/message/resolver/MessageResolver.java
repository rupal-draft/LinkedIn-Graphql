package com.linkedIn.LinkedIn.App.message.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.message.dto.*;
import com.linkedIn.LinkedIn.App.message.dto.records.SendMessageInput;
import com.linkedIn.LinkedIn.App.message.service.MessageService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class MessageResolver {

    private final MessageService messageService;

    public MessageResolver(MessageService messageService) {
        this.messageService = messageService;
    }

    @MutationMapping
    public SingleChatSessionResponse createOrGetChatSession(@Argument Long receiverId) {
        ChatSessionDto chatSessionDto = messageService.createOrGetChatSession(receiverId);
        return new SingleChatSessionResponse("Chat session created or retrieved successfully", true, chatSessionDto);
    }

    @QueryMapping
    public ChatSessionsResponse getMyChatSessions() {
        List<ChatSessionDto> chatSessionDtos = messageService.getMyChatSessions();
        return new ChatSessionsResponse("Your chat sessions fetched successfully",true,chatSessionDtos.size(),chatSessionDtos);
    }

    @MutationMapping
    public SingleMessageResponse sendMessage(@Argument("input") SendMessageInput input) {
        MessageDto messageDto = messageService.sendMessage(input);
        return new SingleMessageResponse("Message sent successfully",true,messageDto);
    }

    @QueryMapping
    public MessagesResponse getMessagesBySession(@Argument Long sessionId) {
        List<MessageDto> messageDtos = messageService.getMessagesBySession(sessionId);
        int unreadMessages = (int) messageDtos.stream()
                .filter(messageDto -> !messageDto.isSeen())
                .count();
        return new MessagesResponse("Messages fetched successfully",true,messageDtos.size(),unreadMessages,messageDtos);
    }

    @MutationMapping
    public Response markMessagesAsSeen(@Argument Long sessionId) {
        String response = messageService.markMessagesAsSeen(sessionId);
        return new Response(response,true);
    }

    @SubscriptionMapping
    public Flux<MessageDto> subscribeToMessages(@Argument Long sessionId) {
        return messageService.subscribeToMessages(sessionId);
    }
}
