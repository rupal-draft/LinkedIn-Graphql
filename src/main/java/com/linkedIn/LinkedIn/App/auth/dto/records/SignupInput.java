package com.linkedIn.LinkedIn.App.auth.dto.records;

import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupInput(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Size(max = 100, message = "Headline can be up to 100 characters")
        String headline,

        @Size(max = 1000, message = "About section is too long")
        String about,

        @Pattern(
                regexp = "^\\+?[0-9. ()-]{7,25}$",
                message = "Invalid phone number"
        )
        String phone,

        @Size(max = 100, message = "Location can be up to 100 characters")
        String location,

        @Pattern(
                regexp = "^(https?://)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{2,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&/=]*)?$",
                message = "Invalid website URL"
        )
        String website,

        @NotBlank(message = "Role is required")
        Roles role
) {}

