package com.linkedIn.LinkedIn.App.job.service;

import com.linkedIn.LinkedIn.App.job.dto.JobApplicationDto;
import com.linkedIn.LinkedIn.App.job.dto.JobDto;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.job.dto.records.JobUpdate;
import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.JobApplication;
import jakarta.validation.Valid;

import java.util.List;

public interface JobService {

    JobDto createJob(@Valid JobInput jobInput);

    void updateJob(Long jobId, @Valid JobUpdate jobUpdate);

    boolean deleteJob(Long jobId);

    List<JobDto> getAllJobs();

    List<JobDto> getJobsByStatus(String status);

    List<JobDto> getJobsByTitle(String title);

    List<JobDto> getJobsByLocation(String location);

    List<JobDto> getJobsByTitleAndLocation(String title, String location);

    List<JobDto> getJobsByTitleAndStatus(String title, String status);

    List<JobDto> getJobsByLocationAndStatus(String location, String status);

    List<JobDto> getJobsByTitleAndLocationAndStatus(String title, String location, String status);

    JobDto getJobById(Long jobId);

    List<JobDto> getJobsByPostedBy(Long postedBy);

    void applyForJob(Long jobId, String resumeUrl);

    void withdrawApplication(Long jobId);

    void saveJob(Long jobId);

    void unsaveJob(Long jobId);

    List<JobApplicationDto> getMyApplications();

    List<JobApplicationDto> getApplicationsOfJob(Long jobId);

    List<JobApplicationDto> getApplicationsOfJobByStatus(Long jobId, String status);

    JobApplicationDto getJobApplicationById(Long applicationId);

    List<JobDto> getMySavedJobs();

    List<JobDto> recommendJobs();
}
