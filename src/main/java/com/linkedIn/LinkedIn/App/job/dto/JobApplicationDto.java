package com.linkedIn.LinkedIn.App.job.dto;

import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
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

    private Long id;
    private JobDto job;
    private UserDto user;
    private ApplicationStatus applicationStatus;
    private String resumeUrl;
    private LocalDateTime createdAt;
}
