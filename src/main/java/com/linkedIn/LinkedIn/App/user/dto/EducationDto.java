package com.linkedIn.LinkedIn.App.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDto {

    private String school;

    private String degree;

    private String field;

    private LocalDate startDate;

    private LocalDate endDate;

    private String grade;

    private String description;
}
