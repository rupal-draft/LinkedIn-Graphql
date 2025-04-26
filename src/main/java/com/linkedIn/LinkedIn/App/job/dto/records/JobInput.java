package com.linkedIn.LinkedIn.App.job.dto.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobInput(
        @NotBlank(message = "Job title is required")
        @Size(max = 100, message = "Job title can have at most 100 characters")
        String title,

        @NotBlank(message = "Job description is required")
        @Size(max = 5000, message = "Job description can have at most 5000 characters")
        String description,

        @NotBlank(message = "Location is required")
        @Size(max = 100, message = "Location can have at most 100 characters")
        String location,

        @NotBlank(message = "Company name is required")
        @Size(max = 100, message = "Company name can have at most 100 characters")
        String companyName,

        @NotBlank(message = "Employment type is required")
        @Size(max = 50, message = "Employment type can have at most 50 characters")
        String jobType,

        @NotNull(message = "Salary is required")
        @Size(min = 0, message = "Salary cannot be negative")
        Double salary,

        @NotNull(message = "Job status is required")
        String status
) {
}
