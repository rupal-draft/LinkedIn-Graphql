package com.linkedIn.LinkedIn.App.auth.service;

import com.linkedIn.LinkedIn.App.auth.dto.UserDto;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;

public interface AuthService {
    UserDto signup(SignupInput signupInput);
}
