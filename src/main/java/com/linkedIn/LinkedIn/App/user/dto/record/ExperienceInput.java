package com.linkedIn.LinkedIn.App.user.dto.record;

import java.time.LocalDate;

public record ExperienceInput(
    String company,
    String position,
    LocalDate fromDate,
    LocalDate toDate,
    boolean currentlyWorking,
    String description,
    String location,
    double yearsOfExperience
) {
}
