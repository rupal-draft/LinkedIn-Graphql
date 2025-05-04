package com.linkedIn.LinkedIn.App.message.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.BadRequestException;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.message.dto.ChatSessionDto;
import com.linkedIn.LinkedIn.App.message.dto.MessageDto;
import com.linkedIn.LinkedIn.App.message.dto.records.SendMessageInput;
import com.linkedIn.LinkedIn.App.message.entity.ChatSession;
import com.linkedIn.LinkedIn.App.message.entity.Message;
import com.linkedIn.LinkedIn.App.message.repository.ChatSessionRepository;
import com.linkedIn.LinkedIn.App.message.repository.MessageRepository;
import com.linkedIn.LinkedIn.App.message.service.MessageService;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final Map<Long, Sinks.Many<MessageDto>> sessionSinkMap = new ConcurrentHashMap<>();


    private final MessageRepository messageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ChatSessionDto createOrGetChatSession(Long receiverId) {
        log.info("Creating or getting chat session with receiver ID: {}", receiverId);

        Long userId = SecurityUtils.getLoggedInUser().getId();
        User currentUser = userRepository.findByIdWithConnections(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Logged-in user not found"));

        if (currentUser
                .getConnections()
                .stream()
                .noneMatch(user -> user
                        .getId()
                        .equals(receiverId))) {
            throw new BadRequestException("You both are not connected!");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver user not found with ID: " + receiverId));

        try {
            User user1 = currentUser.getId() < receiver.getId() ? currentUser : receiver;
            User user2 = currentUser.getId() < receiver.getId() ? receiver : currentUser;

            Optional<ChatSession> existingSession = chatSessionRepository.findByUsers(user1, user2);
            ChatSession session = existingSession.orElseGet(() -> {
                log.info("Creating new chat session between {} and {}", user1.getId(), user2.getId());
                ChatSession newSession = ChatSession.builder()
                        .user1(user1)
                        .user2(user2)
                        .build();
                return chatSessionRepository.save(newSession);
            });

            return modelMapper.map(session, ChatSessionDto.class);

        } catch (MappingException ex) {
            log.error("Error while creating or retrieving chat session", ex);
            throw new RuntimeException("Failed to create or retrieve chat session");

        } catch (DataIntegrityViolationException ex) {
            log.error("Database constraint violation during chat session creation", ex);
            throw new RuntimeException("Chat session already exists or data constraint failed");

        } catch (Exception ex) {
            log.error("Unexpected error while creating or retrieving chat session", ex);
            throw new ServiceUnavailableException("Unable to start chat session at the moment. Try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chatSessions", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<ChatSessionDto> getMyChatSessions() {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching chat sessions for user {}", currentUser.getId());
        try {
            List<ChatSession> sessions = chatSessionRepository.findAllByUser(currentUser);
            log.info("Fetched {} chat sessions for user {}", sessions.size(), currentUser.getId());
            return sessions.stream()
                    .map(session -> modelMapper.map(session, ChatSessionDto.class))
                    .collect(Collectors.toList());

        } catch (MappingException ex) {
            log.error("Error while creating or retrieving chat session", ex);
            throw new RuntimeException("Failed to create or retrieve chat session");

        } catch (Exception ex) {
            log.error("Failed to fetch chat sessions for user: {}", currentUser.getId(), ex);
            throw new ServiceUnavailableException("Unable to fetch chat sessions at the moment");
        }
    }

    @Override
    @Transactional
    public MessageDto sendMessage(SendMessageInput input) {
        User sender = SecurityUtils.getLoggedInUser();
        log.info("User {} is sending a message in session {}", sender.getId(), input.sessionId());
        ChatSession session = chatSessionRepository.findById(input.sessionId())
                .orElseThrow(() -> {
                    log.warn("Chat session not found with ID: {}", input.sessionId());
                    return new ResourceNotFoundException("Chat session not found");
                });

        if (!session.getUser1().getId().equals(sender.getId()) &&
                !session.getUser2().getId().equals(sender.getId())) {
            log.warn("User {} is not a participant of session {}", sender.getId(), session.getId());
            throw new AccessDeniedException("You are not authorized to send a message in this session");
        }

        try {
            Message message = Message.builder()
                    .content(input.content())
                    .sentAt(LocalDateTime.now())
                    .sender(sender)
                    .session(session)
                    .seen(false)
                    .build();

            Message savedMessage = messageRepository.save(message);
            log.info("User {} sent a message in session {}", sender.getId(), session.getId());

            UserDto receiver = modelMapper.map(session.getUser2(),UserDto.class);
            MessageDto messageDto = modelMapper.map(savedMessage, MessageDto.class);
            messageDto.setReceiver(receiver);

            log.info("Message sent by user {} in session {}", sender.getId(), session.getId());

            return messageDto;

        } catch (DataIntegrityViolationException ex) {
            log.error("Database constraint violation during chat session creation", ex);
            throw new RuntimeException("Chat session already exists or data constraint failed");

        } catch (MappingException ex) {
            log.error("Error while creating or retrieving chat session", ex);
            throw new RuntimeException("Failed to create or retrieve chat session");

        } catch (Exception e) {
            log.error("Failed to send message in session {} by user {}", input.sessionId(), sender.getId(), e);
            throw new ServiceUnavailableException("Could not send message at the moment");
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "messages", key = "#root.methodName + '_' + #sessionId")
    public List<MessageDto> getMessagesBySession(Long sessionId) {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching messages for session {}", sessionId);
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found with ID: {}", sessionId);
                    return new ResourceNotFoundException("Chat session not found");
                });

        if (!session.getUser1().getId().equals(currentUser.getId()) &&
                !session.getUser2().getId().equals(currentUser.getId())) {
            log.warn("User {} is not a participant of session {}", currentUser.getId(), sessionId);
            throw new AccessDeniedException("You are not authorized to view messages of this session");
        }

        UserDto receiver = modelMapper.map(session.getUser2(), UserDto.class);

        try {
            List<Message> messages = messageRepository.findBySessionOrderBySentAtAsc(session);
            log.info("Fetched {} messages for session {}", messages.size(), sessionId);
            return messages.stream()
                    .map(msg -> {
                        MessageDto messageDto = modelMapper.map(msg, MessageDto.class);
                        messageDto.setReceiver(receiver);
                        return messageDto;
                    })
                    .collect(Collectors.toList());

        } catch (MappingException ex) {
            log.error("Error while creating or retrieving chat session", ex);
            throw new RuntimeException("Failed to create or retrieve chat session");

        } catch (Exception e) {
            log.error("Error fetching messages for session {}", sessionId, e);
            throw new ServiceUnavailableException("Failed to retrieve messages");
        }
    }

    @Override
    @Transactional
    public String markMessagesAsSeen(Long sessionId) {
        User currentUser = SecurityUtils.getLoggedInUser();

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Chat session not found with ID: {}", sessionId);
                    return new ResourceNotFoundException("Chat session not found");
                });

        if (!session.getUser1().getId().equals(currentUser.getId()) &&
                !session.getUser2().getId().equals(currentUser.getId())) {
            log.warn("User {} is not a participant of session {}", currentUser.getId(), sessionId);
            throw new AccessDeniedException("You are not authorized to update seen status in this session");
        }

        int matchingMessages = messageRepository.countUnseenMessagesFromOtherUser(session, currentUser.getId());
        log.info("Found {} unseen messages in session {} sent by others (not user {}).", matchingMessages, session.getId(), currentUser.getId());


        try {
            int updatedCount = messageRepository.markMessagesAsSeen(session, currentUser.getId());
            String response = String.format("Successfully marked %d message(s) as seen in session ID %d by user ID %d.", updatedCount, sessionId, currentUser.getId());
            log.info(response);
            return response;

        } catch (Exception e) {
            log.error("Error marking messages as seen in session {}", sessionId, e);
            throw new ServiceUnavailableException("Could not mark messages as seen");
        }
    }
}
