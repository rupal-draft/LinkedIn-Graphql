package com.linkedIn.LinkedIn.App.auth.service.implementations;

import com.linkedIn.LinkedIn.App.auth.dto.UserDto;
import com.linkedIn.LinkedIn.App.auth.dto.records.SignupInput;
import com.linkedIn.LinkedIn.App.auth.service.AuthService;
import com.linkedIn.LinkedIn.App.common.exceptions.IllegalState;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {


    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto signup(SignupInput signupInput) {

        try {
            if(userRepository.existsByEmail(signupInput.email())) {
                throw new IllegalState("User with email " + signupInput.email() + " already exists.");
            }

            User user = modelMapper.map(signupInput, User.class);
            user.setPassword(passwordEncoder.encode(signupInput.password()));

            userRepository.save(user);

            return modelMapper.map(user, UserDto.class);
        } catch (MappingException e) {
            log.error(e.getMessage());
            throw e;
        } catch (IllegalState e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}
