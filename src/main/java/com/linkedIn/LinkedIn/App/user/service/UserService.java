package com.linkedIn.LinkedIn.App.user.service;

import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getUserFromId(Long userId);
}
