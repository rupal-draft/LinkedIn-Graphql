package com.linkedIn.LinkedIn.App.auth.service;

import com.linkedIn.LinkedIn.App.auth.dto.records.ChangePasswordInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.LoginInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import jakarta.validation.Valid;

public interface AuthService {
    UserDto signup(@Valid SignupInput signupInput);

    String[] login(@Valid LoginInput loginInput);

    boolean changePassword(@Valid ChangePasswordInput changePasswordInput);

    String[] refreshToken(String refreshToken);
}
