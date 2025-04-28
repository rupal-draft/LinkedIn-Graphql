package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.JobApplication;
import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByJobAndUser(Job job, User user);

    List<JobApplication> findByJob(Job job);

    List<JobApplication> findByUser(User user);

    List<JobApplication> findByJobAndApplicationStatus(Job job, ApplicationStatus status);
}
