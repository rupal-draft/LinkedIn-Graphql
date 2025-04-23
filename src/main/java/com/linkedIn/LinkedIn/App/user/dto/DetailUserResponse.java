package com.linkedIn.LinkedIn.App.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailUserResponse {
    private String message;
    private boolean success;
    private DetailedUserDto user;
}
