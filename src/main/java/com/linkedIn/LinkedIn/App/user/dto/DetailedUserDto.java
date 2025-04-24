package com.linkedIn.LinkedIn.App.user.dto;

import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedUserDto {
    private String name;

    private String email;

    private String headline;

    private String about;

    private String profilePicture;

    private String coverPicture;

    private String location;

    private String website;

    private String phone;

    private String currentPosition;

    private Roles role;

    private Set<UserDto> connections;

    private List<ExperienceDto> experienceList;

    private List<EducationDto> educationList;
}
