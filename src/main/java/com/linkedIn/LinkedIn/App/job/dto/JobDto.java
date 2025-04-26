package com.linkedIn.LinkedIn.App.job.dto;

import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobType;
import com.linkedIn.LinkedIn.App.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JobDto {

    private Long id;

    private String title;

    private String description;

    private String location;

    private Double salary;

    private JobType jobType;

    private String companyName;

    private JobStatus status;

    private User postedBy;
}
