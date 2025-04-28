package com.linkedIn.LinkedIn.App.job.service.implementation;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.BadRequestException;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.job.dto.JobApplicationDto;
import com.linkedIn.LinkedIn.App.job.dto.JobDto;
import com.linkedIn.LinkedIn.App.job.dto.records.JobInput;
import com.linkedIn.LinkedIn.App.job.dto.records.JobUpdate;
import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.JobApplication;
import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobType;
import com.linkedIn.LinkedIn.App.job.repository.JobApplicationRepository;
import com.linkedIn.LinkedIn.App.job.repository.JobRepository;
import com.linkedIn.LinkedIn.App.job.repository.SavedJobRepository;
import com.linkedIn.LinkedIn.App.job.service.JobService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.linkedIn.LinkedIn.App.common.utils.UtilityClass.getNullPropertyNames;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    @CacheEvict(value = "jobs", allEntries = true)
    public JobDto createJob(JobInput jobInput) {
        log.info("Request received to create job: {}", jobInput.title());

        if (jobInput == null) {
            log.error("Job input is null");
            throw new IllegalArgumentException("Job details must not be null");
        }

        User currentUser = SecurityUtils.getLoggedInUser();

        try {
            Job job = modelMapper.map(jobInput, Job.class);
            job.setPostedBy(currentUser);

            Job savedJob = jobRepository.save(job);
            log.info("Successfully created job with ID: {}", savedJob.getId());

            return modelMapper.map(savedJob, JobDto.class);

        } catch (Exception ex) {
            log.error("Error occurred while creating job: {}", ex.getMessage(), ex);
            throw new ServiceUnavailableException("Unable to create job at this moment. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "jobs", allEntries = true)
    public void updateJob(Long jobId, JobUpdate jobUpdate) {
        log.info("Request received to update job with ID: {}", jobId);

        if (jobUpdate == null || jobId == null) {
            log.error("Job update request or Job ID is null");
            throw new IllegalArgumentException("Job ID must be provided for update");
        }

        User currentUser = SecurityUtils.getLoggedInUser();

        try {
            Job existingJob = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            if (!existingJob.getPostedBy().getId().equals(currentUser.getId())) {
                log.error("User {} is not authorized to update job posted by user {}", currentUser.getId(), existingJob.getPostedBy().getId());
                throw new AccessDeniedException("You are not allowed to update this job");
            }

            Job jobUpdateEntity = modelMapper.map(jobUpdate, Job.class);

            BeanUtils.copyProperties(jobUpdateEntity, existingJob, getNullPropertyNames(jobUpdateEntity));

            if (jobUpdate.status() != null) {
                existingJob.setStatus(JobStatus.valueOf(jobUpdate.status().toUpperCase()));
            }

            if (jobUpdate.jobType() != null) {
                existingJob.setJobType(JobType.valueOf(jobUpdate.jobType().toUpperCase()));
            }

            jobRepository.save(existingJob);
            log.info("Successfully updated job with ID: {}", existingJob.getId());

        } catch (ResourceNotFoundException | AccessDeniedException | IllegalArgumentException e) {
            log.error("Error occurred while updating job: {}", e.getMessage(), e);
            throw e;
        } catch (Exception ex) {
            log.error("Unexpected error occurred while updating job: {}", ex.getMessage(), ex);
            throw new ServiceUnavailableException("Unable to update job at this moment. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "jobs", allEntries = true)
    public boolean deleteJob(Long jobId) {
        log.info("Request received to delete job with ID: {}", jobId);

        try {
            User currentUser = SecurityUtils.getLoggedInUser();

            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            if (!job.getPostedBy().getId().equals(currentUser.getId())) {
                log.error("User {} is not authorized to delete job with ID {}", currentUser.getId(), jobId);
                throw new AccessDeniedException("You are not allowed to delete this job.");
            }

            jobRepository.delete(job);
            log.info("Successfully deleted job with ID: {}", jobId);

            return true;
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            log.error("Error occurred while deleting job with ID {}: {}", jobId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting job with ID {}: {}", jobId, e.getMessage(), e);
            throw new ServiceUnavailableException("Unable to delete job at this moment. Please try again later.");
        }
    }

    @Override
    @Cacheable(value = "jobs")
    @Transactional(readOnly = true)
    public List<JobDto> getAllJobs() {
        log.info("Fetching all jobs");

        List<Job> jobs = jobRepository.findAll();
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "#status")
    public List<JobDto> getJobsByStatus(String status) {
        log.info("Fetching all jobs by status : {}",status);

        List<Job> jobs = jobRepository
                .findByStatus(JobStatus
                        .valueOf(status
                                .toUpperCase()));
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "#title")
    public List<JobDto> getJobsByTitle(String title) {
        log.info("Fetching all jobs by tittle : {}",title);

        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCase(title);
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "#location")
    public List<JobDto> getJobsByLocation(String location) {
        log.info("Fetching all jobs by location : {}",location);

        List<Job> jobs = jobRepository.findByLocationIgnoreCase(location);
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "'title:' + #title + ',location:' + #location")
    public List<JobDto> getJobsByTitleAndLocation(String title, String location) {
        log.info("Fetching all jobs by title : {} and location : {}",title,location);

        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCaseAndLocationIgnoreCase(title,location);
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "'title:' + #title + ',status:' + #status")
    public List<JobDto> getJobsByTitleAndStatus(String title, String status) {
        log.info("Fetching all jobs by title : {} and status : {}",title,status);

        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCaseAndStatus(title, JobStatus.valueOf(status.toUpperCase()));
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "'location:' + #location + ',status:' + #status")
    public List<JobDto> getJobsByLocationAndStatus(String location, String status) {
        log.info("Fetching all jobs by location : {} and status : {}",location,status);

        List<Job> jobs = jobRepository.findByLocationIgnoreCaseAndStatus(location, JobStatus.valueOf(status.toUpperCase()));
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "'title:' + #title + ',location:' + #location + ',status:' + #status")
    public List<JobDto> getJobsByTitleAndLocationAndStatus(String title, String location, String status) {
        log.info("Fetching all jobs by location : {} and status : {} and title : {}",location,status,title);

        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCaseAndLocationIgnoreCaseAndStatus(title, location, JobStatus.valueOf(status.toUpperCase()));
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "job", key = "#jobId")
    public JobDto getJobById(Long jobId) {
        log.info("Fetching job with ID: {}", jobId);

        if (jobId == null) {
            log.error("Job ID cannot be null");
            throw new IllegalArgumentException("Job ID must be provided");
        }

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("No job found with ID: " + jobId));

            JobDto jobDto = modelMapper.map(job, JobDto.class);
            log.info("Successfully fetched and mapped job with ID: {}", jobId);
            return jobDto;

        } catch (MappingException ex) {
            log.error("Error mapping Job entity to DTO for job ID {}: {}", jobId, ex.getLocalizedMessage(), ex);
            throw new RuntimeException("Failed to map job entity to DTO", ex);
        } catch (ResourceNotFoundException ex) {
            log.error("Job not found with ID: {}", jobId);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching job with ID {}: {}", jobId, ex.getLocalizedMessage(), ex);
            throw new RuntimeException("Unexpected error occurred while fetching job", ex);
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobs", key = "#postedBy")
    public List<JobDto> getJobsByPostedBy(Long postedBy) {
        log.info("Fetching user with id : {}",postedBy);
        User user = userRepository
                .findById(postedBy).orElseThrow(() -> new ResourceNotFoundException("No user found with this id : " + postedBy));

        log.info("Fetching all jobs posted by {}",user.getName());

        List<Job> jobs = jobRepository.findByPostedBy(user);
        if (jobs.isEmpty()) {
            log.warn("No jobs found in the database.");
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = mapToJobDto(jobs);
        log.info("Successfully fetched {} jobs", jobDtos.size());

        return jobDtos;
    }

    @Override
    public List<JobDto> mapToJobDto(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            log.warn("No jobs to map, returning empty list");
            return Collections.emptyList();
        }

        try {
            return jobs.stream()
                    .map(job -> modelMapper.map(job, JobDto.class))
                    .toList();
        } catch (MappingException e) {
            log.error("Error occurred while mapping jobs to DTOs: {}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed to map job entities to DTOs", e);
        } catch (Exception e) {
            log.error("Unexpected error during job mapping: {}", e.getLocalizedMessage(), e);
            throw new RuntimeException("Unexpected error while mapping jobs", e);
        }
    }


    @Override
    public void applyForJob(Long jobId) {

    }

    @Override
    public void withdrawApplication(Long jobId) {

    }

    @Override
    public void saveJob(Long jobId) {

    }

    @Override
    public void unsaveJob(Long jobId) {

    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "jobApplications", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<JobApplicationDto> getMyApplications() {
        User user = SecurityUtils.getLoggedInUser();
        log.info("Fetching job applications for user ID: {}", user.getId());

        try {
            List<JobApplication> jobApplications = jobApplicationRepository.findByUser(user);

            if (jobApplications.isEmpty()) {
                log.warn("No job applications found for user ID: {}", user.getId());
                return Collections.emptyList();
            }

            List<JobApplicationDto> jobApplicationDtos = mapToJobApplicationDto(jobApplications);
            log.info("Successfully fetched {} job applications", jobApplicationDtos.size());

            return jobApplicationDtos;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching job applications: {}", ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch job applications at the moment. Please try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "applications", key = "'jobId:' + #jobId + ',status:' + #status")
    public List<JobApplication> getApplicationsOfJobByStatus(Long jobId, String status) {
        log.info("Fetching applications for job ID: {} with status: {}", jobId, status);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            ApplicationStatus applicationStatus;
            try {
                applicationStatus = ApplicationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid application status provided: {}", status, e);
                throw new BadRequestException("Invalid application status: " + status);
            }

            List<JobApplication> applications = jobApplicationRepository.findByJobAndApplicationStatus(job, applicationStatus);

            if (applications.isEmpty()) {
                log.warn("No applications found for job ID: {} with status: {}", jobId, status);
                return Collections.emptyList();
            }

            log.info("Successfully fetched {} applications with status {} for job ID: {}", applications.size(), status, jobId);
            return applications;
        } catch (BadRequestException | ResourceNotFoundException ex) {
            log.error("Known error while fetching applications: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching applications: {}", ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch applications at the moment. Please try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "applications", key = "'jobId:' + #jobId")
    public List<JobApplication> getApplicationsOfJob(Long jobId) {
        log.info("Fetching all applications for job ID: {}", jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            List<JobApplication> applications = jobApplicationRepository.findByJob(job);

            if (applications.isEmpty()) {
                log.warn("No applications found for job ID: {}", jobId);
                return Collections.emptyList();
            }

            log.info("Successfully fetched {} applications for job ID: {}", applications.size(), jobId);
            return applications;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching applications for job ID {}: {}", jobId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch applications at the moment. Please try again later.");
        }
    }

    @Override
    public List<JobApplicationDto> mapToJobApplicationDto(List<JobApplication> jobApplications) {
        return jobApplications
                .stream()
                .map(application -> {
                    try {
                        return modelMapper.map(application, JobApplicationDto.class);
                    } catch (MappingException e) {
                        log.error("Error mapping job application: {}", e.getLocalizedMessage(), e);
                        throw e;
                    }
                })
                .toList();
    }

    @Override
    public List<JobDto> getMySavedJobs() {
        return List.of();
    }

    @Override
    public List<JobDto> recommendJobs() {
        return List.of();
    }
}
