package com.linkedIn.LinkedIn.App.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListUserResponseDto {
    private String message;
    private boolean success;
    private int totalUsers;
    private List<UserDto> users;
}
