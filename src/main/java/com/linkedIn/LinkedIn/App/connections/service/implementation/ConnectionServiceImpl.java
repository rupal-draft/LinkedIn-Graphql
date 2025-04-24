package com.linkedIn.LinkedIn.App.connections.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.connections.entity.ConnectionRequest;
import com.linkedIn.LinkedIn.App.connections.entity.enums.RequestStatus;
import com.linkedIn.LinkedIn.App.connections.repository.ConnectionRequestRepository;
import com.linkedIn.LinkedIn.App.connections.service.ConnectionService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @CacheEvict(value = "connections", key = "#receiverId")
    public void sendConnectionRequest(Long receiverId) {
        try {
            log.info("Sending connection request to user {}", receiverId);
            User sender = SecurityUtils.getLoggedInUser();
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
            User receiver = SecurityUtils.getLoggedInUser();
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
            request.setStatus(RequestStatus.ACCEPTED);
            connectionRequestRepository.save(request);

        } catch (ResourceNotFoundException e) {
            log.error("Error accepting connection request: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            throw new RuntimeException("Something went wrong while accepting the request.");
        }
    }
}
