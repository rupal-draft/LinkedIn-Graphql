package com.linkedIn.LinkedIn.App.user.dto.record;

import java.time.LocalDate;

public record EducationInput(
        String school,
        String degree,
        String field,
        LocalDate startDate,
        LocalDate endDate,
        String grade,
        String description
) {
}
