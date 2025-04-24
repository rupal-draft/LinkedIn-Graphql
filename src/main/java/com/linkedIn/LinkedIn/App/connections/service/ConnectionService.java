package com.linkedIn.LinkedIn.App.connections.service;

public interface ConnectionService {

    void sendConnectionRequest(Long receiverId);

    void acceptConnectionRequest(Long requestId);
}
