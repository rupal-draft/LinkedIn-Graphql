package com.linkedIn.LinkedIn.App.job.service;

import com.linkedIn.LinkedIn.App.job.dto.JobApplicationDto;
import com.linkedIn.LinkedIn.App.job.dto.JobDto;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.validation.Valid;

import java.util.List;

public interface JobService {

    void createJob(@Valid JobInput jobInput);

    void updateJob(@Valid JobInput jobInput);

    void deleteJob(Long jobId);

    List<JobDto> getAllJobs();

    List<JobDto> getJobsByStatus(String status);

    List<JobDto> getJobsByTitle(String title);

    List<JobDto> getJobsByLocation(String location);

    List<JobDto> getJobsByTitleAndLocation(String title, String location);

    List<JobDto> getJobsByTitleAndStatus(String title, String status);

    List<JobDto> getJobsByLocationAndStatus(String location, String status);

    List<JobDto> getJobsByTitleAndLocationAndStatus(String title, String location, String status);

    JobDto getJobById(Long jobId);

    List<JobDto> getJobsByPostedBy(User postedBy);

    void applyForJob(Long jobId);

    void withdrawApplication(Long jobId);

    void saveJob(Long jobId);

    void unsaveJob(Long jobId);

    List<JobApplicationDto> getMyApplications();

    List<JobDto> getMySavedJobs();

    List<JobDto> recommendJobs();
}
