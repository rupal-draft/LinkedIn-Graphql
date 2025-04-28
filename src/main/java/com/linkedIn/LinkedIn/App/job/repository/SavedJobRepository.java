package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.SavedJob;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Set<SavedJob> findByUser(User user);

    Optional<SavedJob> findByJobAndUser(Job job, User user);
}
