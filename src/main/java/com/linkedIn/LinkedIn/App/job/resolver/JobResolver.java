package com.linkedIn.LinkedIn.App.job.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.job.dto.*;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.job.dto.records.JobUpdate;
import com.linkedIn.LinkedIn.App.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class JobResolver {

    private final JobService jobService;

    public JobResolver(JobService jobService) {
        this.jobService = jobService;
    }

    @MutationMapping
    public JobResponseDto createJob(@Argument("jobInput") @Valid JobInput jobInput) {
        JobDto jobResponseDto = jobService.createJob(jobInput);
        return new JobResponseDto("Job created successfully", true, jobResponseDto);
    }

    @MutationMapping
    public Response updateJob(@Argument("jobId") Long jobId, @Argument("jobUpdate") @Valid JobUpdate jobUpdate) {
        jobService.updateJob(jobId, jobUpdate);
        return new Response("Job updated successfully", true);
    }

    @MutationMapping
    public Response deleteJob(@Argument("jobId") Long jobId) {
        boolean success = jobService.deleteJob(jobId);
        return new Response("Job deleted successfully", success);
    }

    @MutationMapping
    public Response saveJob(@Argument("jobId") Long jobId) {
        jobService.saveJob(jobId);
        return new Response("Job saved successfully", true);
    }

    @MutationMapping
    public Response unsaveJob(@Argument("jobId") Long jobId) {
        jobService.unsaveJob(jobId);
        return new Response("Job unsaved successfully", true);
    }

    @MutationMapping
    public Response applyForJob(@Argument("jobId") Long jobId, @Argument("resumeUrl") String resumeUrl) {
        jobService.applyForJob(jobId, resumeUrl);
        return new Response("Application submitted successfully", true);
    }

    @MutationMapping
    public Response withdrawApplication(@Argument("jobId") Long jobId) {
        jobService.withdrawApplication(jobId);
        return new Response("Application withdrawn successfully", true);
    }

    @QueryMapping
    public JobResponseDto getJobById(@Argument("jobId") Long jobId) {
        JobDto jobDto = jobService.getJobById(jobId);
        return new JobResponseDto("Job fetched successfully", true, jobDto);
    }

    @QueryMapping
    public JobApplicationsResponse getMyApplications() {
        List<JobApplicationDto> jobApplicationDtos = jobService.getMyApplications();
        return new JobApplicationsResponse("My applications fetched successfully", true, jobApplicationDtos.size(), jobApplicationDtos);
    }

    @QueryMapping
    public JobApplicationsResponse getApplicationsOfJob(@Argument("jobId") Long jobId) {
        List<JobApplicationDto> jobApplicationDtos = jobService.getApplicationsOfJob(jobId);
        return new JobApplicationsResponse("Applications fetched successfully", true, jobApplicationDtos.size(), jobApplicationDtos);
    }

    @QueryMapping
    public JobApplicationsResponse getApplicationsOfJobByStatus(@Argument("jobId") Long jobId, @Argument("status") String status) {
        List<JobApplicationDto> jobApplicationDtos = jobService.getApplicationsOfJobByStatus(jobId, status);
        return new JobApplicationsResponse("Applications fetched successfully", true, jobApplicationDtos.size(), jobApplicationDtos);
    }

    @QueryMapping
    public SingleJobApplicationResponse getApplicationById(@Argument("applicationId") Long applicationId) {
        JobApplicationDto jobApplicationDto = jobService.getJobApplicationById(applicationId);
        return new SingleJobApplicationResponse("Application fetched successfully", true, jobApplicationDto);
    }

    @QueryMapping
    public JobsResponseDto getMySavedJobs() {
        List<JobDto> jobDtos = jobService.getMySavedJobs();
        return new JobsResponseDto("My saved jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByStatus(@Argument("status") String status) {
        List<JobDto> jobDtos = jobService.getJobsByStatus(status);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto recommendedJobs() {
        List<JobDto> jobDtos = jobService.recommendJobs();
        return new JobsResponseDto("Recommended jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByTitle(@Argument("title") String title) {
        List<JobDto> jobDtos = jobService.getJobsByTitle(title);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByTitleAndStatus(@Argument("title") String title, @Argument("status") String status) {
        List<JobDto> jobDtos = jobService.getJobsByTitleAndStatus(title, status);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByTitleAndLocation(@Argument("title") String title, @Argument("location") String location) {
        List<JobDto> jobDtos = jobService.getJobsByTitleAndLocation(title, location);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByTitleAndLocationAndStatus(@Argument("title") String title, @Argument("location") String location, @Argument("status") String status) {
        List<JobDto> jobDtos = jobService.getJobsByTitleAndLocationAndStatus(title, location, status);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByLocation(@Argument("location") String location) {
        List<JobDto> jobDtos = jobService.getJobsByLocation(location);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByLocationAndStatus(@Argument("location") String location, @Argument("status") String status) {
        List<JobDto> jobDtos = jobService.getJobsByLocationAndStatus(location, status);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getJobsByUser(@Argument("email") String email) {
        List<JobDto> jobDtos = jobService.getJobsByPostedBy(email);
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }

    @QueryMapping
    public JobsResponseDto getAllJobs() {
        List<JobDto> jobDtos = jobService.getAllJobs();
        return new JobsResponseDto("Jobs fetched successfully", true, jobDtos.size(), jobDtos);
    }
}
