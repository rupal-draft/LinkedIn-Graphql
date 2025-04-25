package com.linkedIn.LinkedIn.App.connections.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionRequestsResponse {
    private String message;
    private boolean success;
    private int totalRequests;
    private List<ConnectionRequestDto> requests;
}
