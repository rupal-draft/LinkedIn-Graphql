package com.linkedIn.LinkedIn.App.user.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.IllegalState;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.user.dto.DetailedUserDto;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.dto.record.EducationInput;
import com.linkedIn.LinkedIn.App.user.dto.record.ExperienceInput;
import com.linkedIn.LinkedIn.App.user.dto.record.UserUpdateInput;
import com.linkedIn.LinkedIn.App.user.entity.Education;
import com.linkedIn.LinkedIn.App.user.entity.Experience;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.EducationRepository;
import com.linkedIn.LinkedIn.App.user.repository.ExperienceRepository;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import com.linkedIn.LinkedIn.App.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    @Override
    public User getUserFromId(Long userId) {
        log.info("Getting user with id {}", userId);
        return userRepository
                .findById(userId)
                .orElseThrow(()-> new AuthenticationCredentialsNotFoundException("User not found!"));
    }

    @Override
    @Transactional
    public void updateProfile(UserUpdateInput input) {
        log.info("Updating user profile");

        try {
            User user = SecurityUtils.getLoggedInUser();

            Optional.ofNullable(input.name()).ifPresent(user::setName);
            Optional.ofNullable(input.currentPosition()).ifPresent(user::setCurrentPosition);
            Optional.ofNullable(input.location()).ifPresent(user::setLocation);
            Optional.ofNullable(input.profilePicture()).ifPresent(user::setProfilePicture);
            Optional.ofNullable(input.coverPicture()).ifPresent(user::setCoverPicture);
            Optional.ofNullable(input.about()).ifPresent(user::setAbout);
            Optional.ofNullable(input.headline()).ifPresent(user::setHeadline);
            Optional.ofNullable(input.website()).ifPresent(user::setWebsite);
            Optional.ofNullable(input.phone()).ifPresent(user::setPhone);
            Optional.ofNullable(input.role()).ifPresent(user::setRole);
            Optional.ofNullable(input.password())
                    .map(passwordEncoder::encode)
                    .ifPresent(user::setPassword);

            userRepository.save(user);
            log.info("User profile updated successfully for user ID: {}", user.getId());

        } catch (DataIntegrityViolationException ex) {
            log.error("Data integrity violation during profile update: {}", ex.getMessage(), ex);
            throw new RuntimeException("Email or phone number might already be in use.");
        } catch (Exception ex) {
            log.error("Unexpected error while updating user profile: {}", ex.getMessage(), ex);
            throw new RuntimeException("An unexpected error occurred while updating the profile.");
        }
    }

    @Override
    @Transactional
    public void addEducation(EducationInput input) {
        try {
            if (input.startDate().isAfter(input.endDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
            log.info("Adding education for user {}", SecurityUtils.getLoggedInUser().getEmail());
            User user = SecurityUtils.getLoggedInUser();
            Education education = modelMapper.map(input, Education.class);
            education.setUser(user);
            user.getEducationList().add(education);
            educationRepository.save(education);
            log.info("Added education for user {}", user.getEmail());
        } catch (IllegalState e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (ServiceUnavailableException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void addExperience(ExperienceInput input) {
        try{
            if (input.fromDate().isAfter(input.toDate())) {
                throw new IllegalArgumentException("Start date cannot be after end date.");
            }
            log.info("Adding experience for user {}", SecurityUtils.getLoggedInUser().getEmail());
            User user = SecurityUtils.getLoggedInUser();
            Experience experience = modelMapper.map(input, Experience.class);
            experience.setUser(user);
            user.getExperienceList().add(experience);
            experienceRepository.save(experience);
            log.info("Added experience for user {}", user.getEmail());
        } catch (IllegalState e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (ServiceUnavailableException e) {
            log.error(e.getMessage());
            throw new ServiceUnavailableException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteExperience(Long experienceId) {
        try {
            if (experienceId == null) {
                throw new IllegalArgumentException("Experience ID cannot be null.");
            }
            log.info("Deleting experience with id: {}", experienceId);
            User user = SecurityUtils.getLoggedInUser();
            Experience experience = user.getExperienceList().stream(
                    ).filter(e -> e.getId().equals(experienceId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + experienceId));
            user.getExperienceList().remove(experience);
            experienceRepository.delete(experience);
            log.info(
                    "Removed experience with id: {} for user: {}",
                    experienceId,
                    user.getEmail()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteEducation(Long educationId) {
        try {
            if (educationId == null) {
                throw new IllegalArgumentException("Education ID cannot be null.");
            }
            log.info("Deleting education with id: {}", educationId);
            User user = SecurityUtils.getLoggedInUser();
            Education education = user.getEducationList().stream(
                    ).filter(e -> e.getId().equals(educationId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Education not found with id: " + educationId));
            user.getEducationList().remove(education);
            educationRepository.delete(education);
            log.info(
                    "Removed education with id: {} for user: {}",
                    educationId,
                    user.getEmail()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "usersByCompanyOrExperience", key = "#company + '-' + #yearsOfExperience")
    public List<UserDto> findUsersByPositionOrExperience(String position, double yearsOfExperience) {
        return experienceRepository
                .findUsersByPositionOrExperience(position, yearsOfExperience)
                .stream()
                .map(user -> {
            try {
                return modelMapper.map(user, UserDto.class);
            } catch (MappingException e) {
                log.error(e.getLocalizedMessage());
                throw new RuntimeException(e.getMessage());
            }
        }).toList();
    }

    @Override
    @Cacheable(value = "usersByEducationFieldOrDegree", key = "#field + '-' + #degree")
    public List<UserDto> findUsersByEducationFieldOrDegree(String field, String degree) {
        List<User> users = educationRepository.findUsersByEducationFieldOrDegree(field, degree);
        if (users != null && !users.isEmpty()) {
            return users
                    .stream()
                    .map(user -> {
                try {
                    return modelMapper.map(user, UserDto.class);
                } catch (MappingException e) {
                    log.error(e.getLocalizedMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }).toList();
        }
        return List.of();
    }

    @Override
    @Cacheable(value = "user", key = "#userId")
    public DetailedUserDto getDetailedUser(Long userId) {
        try {
            User user = userRepository
                    .findById(userId)
                    .orElseThrow(()-> new ResourceNotFoundException("User not found"));
            return modelMapper.map(user, DetailedUserDto.class);
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (MappingException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DetailedUserDto getMyProfile() {
        try {
            String email = SecurityUtils.getLoggedInUser().getEmail();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return modelMapper.map(user, DetailedUserDto.class);
        } catch (MappingException e) {
            log.error(e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user with email {}", username);
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
