package com.linkedIn.LinkedIn.App.connections.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.connections.service.ConnectionService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

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
}
