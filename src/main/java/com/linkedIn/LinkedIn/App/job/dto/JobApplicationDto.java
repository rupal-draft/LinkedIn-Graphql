package com.linkedIn.LinkedIn.App.job.dto;

import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApplicationDto {

    private Long applicationId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Double salary;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
