package com.linkedIn.LinkedIn.App.job.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.job.dto.JobDto;
import com.linkedIn.LinkedIn.App.job.dto.JobResponseDto;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.job.dto.records.JobUpdate;
import com.linkedIn.LinkedIn.App.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class JobResolver {

    private JobService jobService;

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

}
