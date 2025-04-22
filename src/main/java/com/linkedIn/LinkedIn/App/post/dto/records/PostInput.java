package com.linkedIn.LinkedIn.App.post.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record PostInput(

        @NotBlank(message = "Post content must not be empty.")
        @Size(max = 3000, message = "Content cannot exceed 3000 characters.")
        String content,

        @NotBlank(message = "Category must not be empty.")
        String category,

        @URL(message = "Provided URL is not valid.")
        String imageUrl
) {
}

