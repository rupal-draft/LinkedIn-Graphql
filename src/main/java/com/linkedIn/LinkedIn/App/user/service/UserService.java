package com.linkedIn.LinkedIn.App.user.service;

import com.linkedIn.LinkedIn.App.user.dto.DetailedUserDto;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.dto.record.EducationInput;
import com.linkedIn.LinkedIn.App.user.dto.record.ExperienceInput;
import com.linkedIn.LinkedIn.App.user.dto.record.UserUpdateInput;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getUserFromId(Long userId);

    void updateProfile(@Valid UserUpdateInput input);

    void addEducation(@Valid EducationInput input);

    void addExperience(@Valid ExperienceInput input);

    void deleteExperience(Long experienceId);

    void deleteEducation(Long educationId);

    List<UserDto> findUsersByCompanyOrExperience(String company, double yearsOfExperience);

    List<UserDto> findUsersByEducationFieldOrDegree(String field, String degree);

    DetailedUserDto getDetailedUser(Long userId);

    DetailedUserDto getMyProfile();
}
