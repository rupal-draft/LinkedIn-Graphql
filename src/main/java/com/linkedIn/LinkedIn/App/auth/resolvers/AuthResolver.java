package com.linkedIn.LinkedIn.App.auth.resolvers;


import com.linkedIn.LinkedIn.App.auth.dto.LoginResponse;
import com.linkedIn.LinkedIn.App.auth.dto.SignupResponse;
import com.linkedIn.LinkedIn.App.auth.dto.records.ChangePasswordInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.LoginInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;
import com.linkedIn.LinkedIn.App.auth.service.AuthService;
import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import graphql.schema.DataFetchingEnvironment;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.Duration;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthResolver {

    @Value("${deploy.env}")
    private String deployEnv;
    private final AuthService authService;

    @MutationMapping
    public SignupResponse signup(@Argument("input") @Valid SignupInput signupInput) {
        UserDto user = authService.signup(signupInput);
        return new SignupResponse("User created successfully", true, user);
    }


    @MutationMapping
    public LoginResponse login(@Argument("input") @Valid LoginInput loginInput, DataFetchingEnvironment env) {
        String[] tokens = authService.login(loginInput);

        HttpServletResponse httpServletResponse = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        }

        if (httpServletResponse != null) {
            Cookie cookie = new Cookie("refreshToken", tokens[1]);
            cookie.setHttpOnly(true);
            cookie.setSecure(deployEnv.equals("production"));
            cookie.setPath("/graphql");
            httpServletResponse.addCookie(cookie);
        } else {
            log.warn("HttpServletResponse not available in the current context.");
        }

        return new LoginResponse("User logged in successfully", true, tokens[0]);
    }

    @MutationMapping
    public Response changePassword(@Argument("input") @Valid ChangePasswordInput changePasswordInput) {
        boolean success = authService.changePassword(changePasswordInput);
        return new Response("Password changed successfully", success);
    }

    @MutationMapping
    public LoginResponse refreshToken(DataFetchingEnvironment env) {
        HttpServletRequest httpServletRequest = null;
        HttpServletResponse httpServletResponse = null;

        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            httpServletRequest = requestAttributes.getRequest();
            httpServletResponse = requestAttributes.getResponse();
        }

        if (httpServletRequest == null || httpServletResponse == null) {
            throw new IllegalStateException("HttpServletRequest or HttpServletResponse not available in the current context.");
        }

        String refreshToken = Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Refresh token not found"));

        String[] tokens = authService.refreshToken(refreshToken);
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
        cookie.setSecure(deployEnv.equals("production"));
        cookie.setPath("/graphql");
        int maxAge = (int) Duration.ofDays(180).getSeconds();
        cookie.setMaxAge(maxAge);
        httpServletResponse.addCookie(cookie);

        return new LoginResponse("Token refreshed successfully", true, tokens[0]);
    }
}
