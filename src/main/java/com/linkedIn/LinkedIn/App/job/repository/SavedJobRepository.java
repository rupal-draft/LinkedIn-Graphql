package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    Optional<SavedJob> findByJobIdAndUserId(Long jobId, Long userId);

    List<SavedJob> findByUserId(Long userId);
}
