package com.linkedIn.LinkedIn.App.user.dto.record;

import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UserUpdateInput(
        @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
        String name,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Size(max = 100, message = "Headline can be up to 100 characters")
        String headline,

        @Size(max = 1000, message = "About section is too long")
        String about,

        @URL(message = "Invalid URL")
        String profilePicture,

        @URL(message = "Invalid URL")
        String coverPicture,

        @Size(max = 100, message = "Location can be up to 100 characters")
        String location,

        @URL(message = "Invalid URL")
        String website,

        @Pattern(
                regexp = "^\\+?[0-9. ()-]{7,25}$",
                message = "Invalid phone number"
        )
        String phone,

        @Size(max = 100, message = "Current position can be up to 100 characters")
        String currentPosition,

        Roles role
) {
}
