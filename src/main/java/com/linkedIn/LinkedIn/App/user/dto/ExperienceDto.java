package com.linkedIn.LinkedIn.App.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDto {

    private String company;

    private String position;

    private String location;

    private LocalDate fromDate;

    private LocalDate toDate;

    private boolean currentlyWorking;

    private double yearsOfExperience;

    private String description;
}
