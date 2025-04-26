package com.linkedIn.LinkedIn.App.job.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplicationsResponse {
    private String message;
    private boolean success;
    private int totalApplications;
    private List<JobApplicationDto> applications;
}
