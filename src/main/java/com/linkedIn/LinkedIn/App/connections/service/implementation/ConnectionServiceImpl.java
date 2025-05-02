package com.linkedIn.LinkedIn.App.connections.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.connections.dto.ConnectionRequestDto;
import com.linkedIn.LinkedIn.App.connections.entity.ConnectionRequest;
import com.linkedIn.LinkedIn.App.connections.entity.enums.RequestStatus;
import com.linkedIn.LinkedIn.App.connections.repository.ConnectionRequestRepository;
import com.linkedIn.LinkedIn.App.connections.service.ConnectionService;
import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.notification.service.NotificationService;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @CacheEvict(value = "connections", key = "#receiverId")
    public void sendConnectionRequest(Long receiverId) {
        try {
            log.info("Sending connection request to user {}", receiverId);
            User currentUser = SecurityUtils.getLoggedInUser();
            User sender = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

            if (sender.getId().equals(receiverId)) {
                log.error("Cannot send request to yourself");
                throw new IllegalArgumentException("Cannot send request to yourself");
            }

            if (sender.getConnections().contains(receiver)) {
                log.error("You are already connected");
                throw new IllegalStateException("You are already connected");
            }

            connectionRequestRepository.findBySenderIdAndReceiverId(sender.getId(), receiverId)
                    .ifPresent(request -> {
                        if (request.getStatus() == RequestStatus.PENDING) {
                            log.error("Request already sent");
                            throw new IllegalStateException("Request already sent");
                        }
                    });

            ConnectionRequest request = ConnectionRequest.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .status(RequestStatus.PENDING)
                    .build();
            connectionRequestRepository.save(request);

            log.info("Sending notification to user {}", receiverId);
            notificationService.createNotification("New connection request",
                    "You have a new connection request from " + sender.getName(),
                    receiver,
                    NotificationType.CONNECTION);

            log.info("Saved connection request {}", request);
        } catch (ResourceNotFoundException e) {
            log.error("Error sending connection request: {}", e.getMessage());
            throw new RuntimeException(e.getLocalizedMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Unexpected error sending connection request: {}", e.getMessage());
            throw new ServiceUnavailableException("Something went wrong while sending the request.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Something went wrong while sending the request.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "connections", key = "#requestId")
    public void acceptConnectionRequest(Long requestId) {
        try {
            User currentUser = SecurityUtils.getLoggedInUser();
            User receiver = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));
            ConnectionRequest request = connectionRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

            if (!request.getReceiver().getId().equals(receiver.getId())) {
                throw new AccessDeniedException("You are not authorized to accept this request.");
            }

            if (request.getStatus() != RequestStatus.PENDING) {
                throw new IllegalArgumentException("This connection request is not pending.");
            }

            User sender = request.getSender();

            receiver.getConnections().add(sender);
            sender.getConnections().add(receiver);

            userRepository.save(receiver);
            userRepository.save(sender);
            connectionRequestRepository.updateConnectionRequestStatus(requestId, RequestStatus.ACCEPTED);

            log.info("Sending notification to user {}", sender.getId());
            notificationService.createNotification(
                    "Connection request accepted",
                    receiver.getName() + " accepted your connection request",
                    sender,
                    NotificationType.CONNECTION
            );

            log.info("Successfully accepted connection request with id {}", requestId);

        } catch (ResourceNotFoundException e) {
            log.error("Error accepting connection request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Something went wrong while accepting the request.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "connections", key = "#requestId")
    public void rejectConnectionRequest(Long requestId) {
        log.info("Rejecting connection request with id {}", requestId);
        try {
            ConnectionRequest request = connectionRequestRepository.findById(requestId)
                    .orElseThrow(() -> {
                        log.error("Connection request with id {} not found", requestId);
                        return new ResourceNotFoundException("Connection request not found");
                    });

            if (request.getStatus() != RequestStatus.PENDING) {
                log.error("Connection request with id {} is not pending. Current status: {}", requestId, request.getStatus());
                throw new IllegalStateException("Only pending requests can be rejected.");
            }

            connectionRequestRepository.deleteById(requestId);
            log.info("Successfully deleted connection request with id {}", requestId);

        } catch (ResourceNotFoundException | IllegalStateException e) {
            log.error("Error rejecting connection request with id {}: {}", requestId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while rejecting connection request with id {}: {}", requestId, e.getMessage());
            throw new RuntimeException("Something went wrong while rejecting the request.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "connections", key = "#connectionId")
    public void removeConnection(Long connectionId) {
        log.info("Removing connection with user id {}", connectionId);

        try {
            Long currentUserId = SecurityUtils.getLoggedInUser().getId();
            User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> {
                log.error("User with id {} not found", currentUserId);
                return new ResourceNotFoundException("User not found");
            });
            User targetUser = userRepository.findById(connectionId)
                    .orElseThrow(() -> {
                        log.error("User with id {} not found", connectionId);
                        return new ResourceNotFoundException("User not found");
                    });

            boolean removedFromCurrent = currentUser.getConnections().remove(targetUser);
            boolean removedFromTarget = targetUser.getConnections().remove(currentUser);

            if (!removedFromCurrent && !removedFromTarget) {
                log.warn("Users {} and {} were not connected", currentUser.getId(), connectionId);
                throw new IllegalStateException("Users are not connected.");
            }

            userRepository.save(currentUser);
            userRepository.save(targetUser);

            log.info("Successfully removed connection between user {} and user {}", currentUser.getId(), connectionId);

        } catch (ResourceNotFoundException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while removing connection with user {}: {}", connectionId, e.getMessage());
            throw new RuntimeException("Something went wrong while removing the connection.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "connections", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<UserDto> getMyConnections() {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching connections for user {}", currentUser.getEmail());

        try {
            Set<User> connections = userRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                    .getConnections();

            if (connections.isEmpty()) {
                log.info("No connections found for user {}", currentUser.getEmail());
            }

            return connections.stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching connections: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch connections.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "connections", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<ConnectionRequestDto> getPendingRequests() {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching pending connection requests received by user {}", currentUser.getEmail());

        try {
            List<ConnectionRequest> pendingRequests = connectionRequestRepository
                    .findAllByReceiverIdAndStatus(currentUser.getId(), RequestStatus.PENDING);
            log.info("Fetched {} pending requests for {}", pendingRequests.size(), currentUser.getEmail());
            return pendingRequests
                .stream()
                .map(this::mapToConnectionRequestDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching pending connection requests: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch pending connection requests.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "connections", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<ConnectionRequestDto> getSentRequests() {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("Fetching pending connection requests sent by user {}", currentUser.getEmail());

        try {
            List<ConnectionRequest> sentRequests = connectionRequestRepository
                    .findAllBySenderIdAndStatus(currentUser.getId(), RequestStatus.PENDING);
            log.info("Fetched {} sent requests for {}", sentRequests.size(), currentUser.getEmail());
            return sentRequests
                .stream()
                .map(this::mapToConnectionRequestDto)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching sent connection requests: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch pending connection requests.");
        }
    }

    private ConnectionRequestDto mapToConnectionRequestDto(ConnectionRequest request) {
        return ConnectionRequestDto.builder()
                .id(request.getId())
                .senderId(request.getSender().getId())
                .senderName(request.getSender().getName())
                .senderEmail(request.getSender().getEmail())
                .receiverId(request.getReceiver().getId())
                .receiverName(request.getReceiver().getName())
                .receiverEmail(request.getReceiver().getEmail())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
