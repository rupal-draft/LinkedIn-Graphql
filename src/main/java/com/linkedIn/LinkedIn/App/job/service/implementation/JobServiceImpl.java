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
import com.linkedIn.LinkedIn.App.job.entity.SavedJob;
import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobType;
import com.linkedIn.LinkedIn.App.job.repository.JobApplicationRepository;
import com.linkedIn.LinkedIn.App.job.repository.JobRepository;
import com.linkedIn.LinkedIn.App.job.repository.SavedJobRepository;
import com.linkedIn.LinkedIn.App.job.service.JobService;
import com.linkedIn.LinkedIn.App.user.entity.Experience;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
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

import java.util.*;
import java.util.stream.Collectors;

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

        if (!(currentUser.getRole().equals(Roles.ADMIN) ||
                currentUser.getRole().equals(Roles.RECRUITER) ||
                currentUser.getRole().equals(Roles.HR))) {

            log.error("User {} is not authorized to create job", currentUser.getId());
            throw new AccessDeniedException("You are not authorized to create job");
        }

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

        if (!(currentUser.getRole().equals(Roles.ADMIN) ||
                currentUser.getRole().equals(Roles.RECRUITER) ||
                currentUser.getRole().equals(Roles.HR))) {

            log.error("User {} is not authorized to update job", currentUser.getId());
            throw new AccessDeniedException("You are not authorized to update job");
        }

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

            if (!(currentUser.getRole().equals(Roles.ADMIN) ||
                    currentUser.getRole().equals(Roles.RECRUITER) ||
                    currentUser.getRole().equals(Roles.HR))) {

                log.error("User {} is not authorized to delete job", currentUser.getId());
                throw new AccessDeniedException("You are not authorized to delete job");
            }

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
    @Cacheable(value = "jobs", key = "#email")
    public List<JobDto> getJobsByPostedBy(String email) {
        log.info("Fetching user with email : {}",email);
        User user = userRepository
                .findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("No user found with this email : " + email));

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
    @Transactional
    @CacheEvict(value = "applications", allEntries = true)
    public void applyForJob(Long jobId, String resumeUrl) {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("User [{}] attempting to apply for job [{}]", currentUser.getId(), jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            boolean alreadyApplied = jobApplicationRepository.findByJobAndUser(job, currentUser).isPresent();
            if (alreadyApplied) {
                log.warn("User [{}] already applied for job [{}]", currentUser.getId(), jobId);
                throw new BadRequestException("You have already applied for this job.");
            }

            JobApplication application = JobApplication.builder()
                    .job(job)
                    .user(currentUser)
                    .resumeUrl(resumeUrl)
                    .applicationStatus(ApplicationStatus.APPLIED)
                    .build();

            jobApplicationRepository.save(application);
            log.info("User [{}] successfully applied for job [{}]", currentUser.getId(), jobId);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            log.error("Known error during job application: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during job application: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to apply for the job. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "applications", allEntries = true)
    public void withdrawApplication(Long jobId) {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("User [{}] attempting to withdraw application for job [{}]", currentUser.getId(), jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            JobApplication application = jobApplicationRepository.findByJobAndUser(job, currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("You have not applied for this job."));

            jobApplicationRepository.delete(application);
            log.info("User [{}] successfully withdrew application for job [{}]", currentUser.getId(), jobId);
        } catch (ResourceNotFoundException ex) {
            log.error("Known error during withdrawal: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during withdrawal: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to withdraw application. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"savedJobs"}, allEntries = true)
    public void saveJob(Long jobId) {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("User [{}] attempting to save job [{}]", currentUser.getId(), jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            boolean alreadySaved = savedJobRepository.findByJobAndUser(job, currentUser).isPresent();
            if (alreadySaved) {
                log.warn("User [{}] already saved job [{}]", currentUser.getId(), jobId);
                throw new BadRequestException("You have already saved this job.");
            }

            SavedJob savedJob = SavedJob.builder()
                    .user(currentUser)
                    .job(job)
                    .build();

            savedJobRepository.save(savedJob);
            log.info("User [{}] successfully saved job [{}]", currentUser.getId(), jobId);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            log.error("Known error during saving job: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during saving job: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to save job. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"savedJobs"}, allEntries = true)
    public void unsaveJob(Long jobId) {
        User currentUser = SecurityUtils.getLoggedInUser();
        log.info("User [{}] attempting to unsave job [{}]", currentUser.getId(), jobId);

        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));

            SavedJob savedJob = savedJobRepository.findByJobAndUser(job, currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("This job is not saved."));

            savedJobRepository.delete(savedJob);
            log.info("User [{}] successfully unsaved job [{}]", currentUser.getId(), jobId);
        } catch (ResourceNotFoundException ex) {
            log.error("Known error during unsaving job: {}", ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during unsaving job: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to unsave job. Please try again later.");
        }
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
    public List<JobApplicationDto> getApplicationsOfJobByStatus(Long jobId, String status) {
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
            return mapToJobApplicationDto(applications);
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
    public List<JobApplicationDto> getApplicationsOfJob(Long jobId) {
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
            return mapToJobApplicationDto(applications);
        } catch (MappingException ex) {
            log.error("Error mapping JobApplication entity to DTO for job ID {}: {}", jobId, ex.getLocalizedMessage(), ex);
            throw ex;
        } catch (ResourceNotFoundException ex) {
            log.error("Known error while fetching applications for job ID {}: {}", jobId, ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching applications for job ID {}: {}", jobId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch applications at the moment. Please try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "application", key = "#applicationId")
    public JobApplicationDto getJobApplicationById(Long applicationId) {
        log.info("Fetching application with ID: {}", applicationId);

        try {
            JobApplication application = jobApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));

            log.info("Successfully fetched application with ID: {}", applicationId);
            return modelMapper.map(application, JobApplicationDto.class);
        } catch (MappingException ex) {
            log.error("Error mapping JobApplication entity to DTO for application ID {}: {}", applicationId, ex.getLocalizedMessage(), ex);
            throw ex;
        } catch (ResourceNotFoundException ex) {
            log.error("Known error while fetching application with ID {}: {}", applicationId, ex.getMessage(), ex);
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error while fetching application with ID {}: {}", applicationId, ex.getMessage(), ex);
            throw new RuntimeException("Unable to fetch application at the moment. Please try again later.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "savedJobs",
            key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()"
    )
    public List<JobDto> getMySavedJobs() {
        log.info("Fetching saved jobs for current user.");

        Long userId = SecurityUtils.getLoggedInUser().getId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        log.debug("Fetched current user: {}", currentUser.getEmail());

        Set<SavedJob> savedJobs = Optional.ofNullable(savedJobRepository.findByUser(currentUser))
                .orElse(Collections.emptySet());

        if (savedJobs.isEmpty()) {
            log.warn("No saved jobs found for user: {}", currentUser.getEmail());
            return Collections.emptyList();
        }

        List<JobDto> savedJobDtos = savedJobs.stream()
                .map(savedJob -> modelMapper.map(savedJob.getJob(), JobDto.class))
                .collect(Collectors.toList());

        log.info("Found {} saved jobs for user {}", savedJobDtos.size(), currentUser.getEmail());
        return savedJobDtos;
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "recommendedJobs",
            key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()"
    )
    public List<JobDto> recommendJobs() {
        log.info("Starting job recommendation process.");

        Long userId = SecurityUtils.getLoggedInUser().getId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new ResourceNotFoundException("User not found with id: " + userId);
                });

        log.debug("Fetched current user: {}", currentUser.getEmail());

        Set<String> userCompanies = Optional.ofNullable(currentUser.getExperienceList())
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(Experience::getCompany)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        double totalYears = Optional.ofNullable(currentUser.getExperienceList())
                .orElse(List.of())
                .stream()
                .mapToDouble(Experience::getYearsOfExperience)
                .sum();

        String userLocation = Optional.ofNullable(currentUser.getLocation()).orElse("");

        Set<Long> excludedJobIds = getExcludedJobIds(currentUser);

        List<Job> recommendedJobs = jobRepository.findRecommendedJobs(
                userCompanies.isEmpty() ? Set.of("-") : userCompanies,
                userLocation,
                totalYears,
                excludedJobIds.isEmpty() ? Set.of(-1L) : excludedJobIds
        );

        if (recommendedJobs.isEmpty()) {
            log.warn("No recommended jobs found for user: {}", currentUser.getEmail());
            return Collections.emptyList();
        }

        List<JobDto> jobDtos = recommendedJobs.stream()
                .map(job -> modelMapper.map(job, JobDto.class))
                .collect(Collectors.toList());

        log.info("Recommended {} jobs for user {}", jobDtos.size(), currentUser.getEmail());
        return jobDtos;
    }


    private Set<Long> getExcludedJobIds(User user) {
        Set<Long> appliedJobIds = Optional.ofNullable(jobApplicationRepository.findByUser(user))
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(jobApplication -> jobApplication.getJob().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> savedJobIds = Optional.ofNullable(savedJobRepository.findByUser(user))
                .orElse(Collections.emptySet())
                .stream()
                .filter(Objects::nonNull)
                .map(savedJob -> savedJob.getJob().getId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> excludedJobIds = new HashSet<>(appliedJobIds);
        excludedJobIds.addAll(savedJobIds);

        log.debug("User {} has {} excluded jobs", user.getEmail(), excludedJobIds.size());
        return excludedJobIds;
    }


    private List<JobApplicationDto> mapToJobApplicationDto(List<JobApplication> jobApplications) {
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

    private List<JobDto> mapToJobDto(List<Job> jobs) {
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
}
