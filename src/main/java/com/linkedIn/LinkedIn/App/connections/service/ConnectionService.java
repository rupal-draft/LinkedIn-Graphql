package com.linkedIn.LinkedIn.App.connections.service;

import com.linkedIn.LinkedIn.App.connections.dto.ConnectionRequestDto;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;

import java.util.List;

public interface ConnectionService {

    void sendConnectionRequest(Long receiverId);

    void acceptConnectionRequest(Long requestId);

    void rejectConnectionRequest(Long requestId);

    void removeConnection(Long connectionId);

    List<UserDto> getMyConnections();

    List<ConnectionRequestDto> getPendingRequests();

    List<ConnectionRequestDto> getSentRequests();
}
