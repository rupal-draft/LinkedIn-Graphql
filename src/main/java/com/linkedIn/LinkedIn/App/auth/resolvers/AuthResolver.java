package com.linkedIn.LinkedIn.App.auth.resolvers;


import com.linkedIn.LinkedIn.App.auth.dto.UserDto;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;
import com.linkedIn.LinkedIn.App.auth.service.AuthService;
import com.linkedIn.LinkedIn.App.common.advices.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthResolver {

    private final AuthService authService;

    public AuthResolver(AuthService authService) {
        this.authService = authService;
    }

    @MutationMapping
    public ApiResponse<UserDto> signup(@Valid SignupInput signupInput) {
        return ApiResponse.success("User created successfully!!",authService.signup(signupInput));
    }
}
