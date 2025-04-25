package com.linkedIn.LinkedIn.App.connections.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.connections.dto.ConnectionRequestDto;
import com.linkedIn.LinkedIn.App.connections.dto.ConnectionRequestsResponse;
import com.linkedIn.LinkedIn.App.connections.dto.ConnectionsResponse;
import com.linkedIn.LinkedIn.App.connections.service.ConnectionService;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ConnectionResolver {

    private final ConnectionService connectionService;

    public ConnectionResolver(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @MutationMapping
    public Response sendConnectionRequest(@Argument Long receiverId) {
        connectionService.sendConnectionRequest(receiverId);
        return new Response("Connection request sent successfully", true);
    }

    @MutationMapping
    public Response acceptConnectionRequest(@Argument Long requestId) {
        connectionService.acceptConnectionRequest(requestId);
        return new Response("Connection request accepted successfully", true);
    }

    @MutationMapping
    public Response rejectConnectionRequest(@Argument Long requestId) {
        connectionService.rejectConnectionRequest(requestId);
        return new Response("Connection request rejected successfully", true);
    }

    @MutationMapping
    public Response removeConnection(@Argument Long connectionId) {
        connectionService.removeConnection(connectionId);
        return new Response("Connection removed successfully", true);
    }

    @QueryMapping
    public ConnectionsResponse getMyConnections() {
        List<UserDto> connections = connectionService.getMyConnections();
        return new ConnectionsResponse("Connections fetched successfully",
                true,
                connections.size(),
                connections);
    }

    @QueryMapping
    public ConnectionRequestsResponse getPendingRequests() {
        List<ConnectionRequestDto> pendingRequests = connectionService.getPendingRequests();
        return new ConnectionRequestsResponse("Pending connection requests fetched successfully",
                true,
                pendingRequests.size(),
                pendingRequests);
    }

    @QueryMapping
    public ConnectionRequestsResponse getSentRequests() {
        List<ConnectionRequestDto> sentRequests = connectionService.getSentRequests();
        return new ConnectionRequestsResponse("Sent connection requests fetched successfully",
                true,
                sentRequests.size(),
                sentRequests);
    }
}
