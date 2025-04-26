package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.JobApplication;
import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByJobIdAndUserId(Long jobId, Long userId);

    List<JobApplication> findByUserId(Long userId);

    List<JobApplication> findByJobIdAndApplicationStatus(Long jobId, ApplicationStatus status);
}
