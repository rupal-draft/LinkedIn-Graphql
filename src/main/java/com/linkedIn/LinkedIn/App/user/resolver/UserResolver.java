package com.linkedIn.LinkedIn.App.user.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.user.dto.DetailUserResponse;
import com.linkedIn.LinkedIn.App.user.dto.ListUserResponseDto;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import com.linkedIn.LinkedIn.App.user.dto.record.EducationInput;
import com.linkedIn.LinkedIn.App.user.dto.record.ExperienceInput;
import com.linkedIn.LinkedIn.App.user.dto.record.UserUpdateInput;
import com.linkedIn.LinkedIn.App.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @MutationMapping
    public Response updateProfile(@Argument("input") @Valid UserUpdateInput input) {
        userService.updateProfile(input);
        return new Response("Profile updated successfully", true);
    }

    @MutationMapping
    public Response addExperience(@Argument("input") @Valid ExperienceInput input) {
        userService.addExperience(input);
        return new Response("Experience added successfully", true);
    }

    @MutationMapping
    public Response addEducation(@Argument("input") @Valid EducationInput input) {
        userService.addEducation(input);
        return new Response("Education added successfully", true);
    }

    @MutationMapping
    public Response deleteExperience(@Argument("experienceId") Long experienceId) {
        userService.deleteExperience(experienceId);
        return new Response("Experience deleted successfully", true);
    }

    @MutationMapping
    public Response deleteEducation(@Argument("educationId") Long educationId) {
        userService.deleteEducation(educationId);
        return new Response("Education deleted successfully", true);
    }

    @QueryMapping
    public ListUserResponseDto filterUsersExperience(@Argument("position") String position, @Argument("years") double years) {
        List<UserDto> users = userService.findUsersByPositionOrExperience(position, years);
        return new ListUserResponseDto("Users filtered successfully based on experience", true,users.size(), users);
    }

    @QueryMapping
    public ListUserResponseDto filterUsersEducation(@Argument("field") String field, @Argument("degree") String degree) {
        List<UserDto> users = userService.findUsersByEducationFieldOrDegree(field, degree);
        return new ListUserResponseDto("Users filtered successfully based on education", true,users.size(), users);
    }

    @QueryMapping
    public DetailUserResponse getUserById(@Argument("id") Long id) {
        return new DetailUserResponse("User fetched successfully", true, userService.getDetailedUser(id));
    }

    @QueryMapping
    public DetailUserResponse getMyProfile() {
        return new DetailUserResponse("Your profile fetched successfully", true, userService.getMyProfile());
    }
}
