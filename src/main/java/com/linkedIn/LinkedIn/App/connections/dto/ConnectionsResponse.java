package com.linkedIn.LinkedIn.App.connections.dto;

import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionsResponse {
    private String message;
    private boolean success;
    private int connectionsCount;
    private List<UserDto> connections;
}
