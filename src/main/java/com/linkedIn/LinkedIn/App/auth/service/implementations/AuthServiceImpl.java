package com.linkedIn.LinkedIn.App.auth.service.implementations;

import com.linkedIn.LinkedIn.App.auth.dto.records.ChangePasswordInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.LoginInput;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;
import com.linkedIn.LinkedIn.App.auth.security.JwtService;
import com.linkedIn.LinkedIn.App.auth.service.AuthService;
import com.linkedIn.LinkedIn.App.common.exceptions.IllegalState;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import com.linkedIn.LinkedIn.App.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    @Transactional
    public UserDto signup(SignupInput signupInput) {
        log.info("Signing up user {}", signupInput.name());
        try {
            if(userRepository.existsByEmail(signupInput.email())) {
                throw new IllegalState("User with email " + signupInput.email() + " already exists.");
            }
            User user = modelMapper.map(signupInput, User.class);
            user.setPassword(passwordEncoder.encode(signupInput.password()));
            log.info("Saving user {}", user);
            userRepository.save(user);

            return modelMapper.map(user, UserDto.class);
        } catch (MappingException e) {
            log.error(e.getMessage());
            throw e;
        } catch (IllegalState e) {
            log.error(e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException exception){
            log.error(exception.getMessage());
            throw new IllegalState("User with email " + signupInput.email() + " already exists.");
        }
    }

    @Override
    public String[] login(LoginInput loginInput) {
        log.info("Logging in user {}", loginInput.email());
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInput.email(), loginInput.password())
            );

            User user = (User) authentication.getPrincipal();
            log.info("Logged in user {}", user.getName());

            String accessToken = jwtService.getAccessJwtToken(user);
            String refreshToken = jwtService.getRefreshJwtToken(user);

            return new String[]{accessToken, refreshToken};
        } catch (BadCredentialsException | DisabledException | UsernameNotFoundException | LockedException e) {
            log.error(e.getMessage());
            throw e;
        } catch (ClassCastException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean changePassword(ChangePasswordInput changePasswordInput) {
        log.info("Changing password for user {}", changePasswordInput.email());
        try {
            User user = userRepository
                    .findByEmail(changePasswordInput.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!passwordEncoder.matches(changePasswordInput.oldPassword(), user.getPassword())) {
                throw new BadCredentialsException("Old password didn't match");
            }

            user.setPassword(passwordEncoder.encode(changePasswordInput.newPassword()));
            return true;

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while changing password: {}", e.getMessage());
            throw new RuntimeException("Something went wrong while changing password");
        }
    }

    @Override
    public String[] refreshToken(String refreshToken) {
        log.info("Refreshing token");
        Long userId = jwtService.getUserId(refreshToken);
        log.info("Refreshing token for user {}", userId);
        User user = userService.getUserFromId(userId);
        String accessToken = jwtService.getAccessJwtToken(user);
        String newRefreshToken = jwtService.getRefreshJwtToken(user);
        log.info("Refreshed token for user {}", userId);
        return new String[]{accessToken, newRefreshToken};
    }
}
