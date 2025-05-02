package com.linkedIn.LinkedIn.App.job.dto.records;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record JobUpdate(
        @Size(max = 100, message = "Job title can have at most 100 characters")
        String title,

        @Size(max = 5000, message = "Job description can have at most 5000 characters")
        String description,

        @Size(max = 100, message = "Location can have at most 100 characters")
        String location,

        @Size(max = 100, message = "Company name can have at most 100 characters")
        String companyName,

        @Size(max = 50, message = "Employment type can have at most 50 characters")
        String jobType,

        @DecimalMin(value = "0.0", inclusive = true, message = "Salary cannot be negative")
        Double salary,

        Double experience,

        String status
) {
}
