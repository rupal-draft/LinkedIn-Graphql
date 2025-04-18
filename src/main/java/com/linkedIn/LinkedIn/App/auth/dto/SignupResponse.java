package com.linkedIn.LinkedIn.App.auth.dto;

import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SignupResponse {
    private String message;
    private boolean success;
    private UserDto user;
}
