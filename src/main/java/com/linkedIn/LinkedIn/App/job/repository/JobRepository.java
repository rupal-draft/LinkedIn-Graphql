package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByPostedBy(User postedBy);

    List<Job> findByLocationIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCase(String keyword);

    List<Job> findByStatus(JobStatus status);

    List<Job> findByLocationIgnoreCaseAndStatus(String location, JobStatus status);

    List<Job> findByTitleContainingIgnoreCaseAndLocationIgnoreCase(String title, String location);

    List<Job> findByTitleContainingIgnoreCaseAndStatus(String title, JobStatus status);

    List<Job> findByTitleContainingIgnoreCaseAndLocationIgnoreCaseAndStatus(
            String title, String location, JobStatus status
    );
}
