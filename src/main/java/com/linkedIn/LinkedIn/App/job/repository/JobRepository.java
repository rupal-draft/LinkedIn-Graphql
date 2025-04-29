package com.linkedIn.LinkedIn.App.job.repository;

import com.linkedIn.LinkedIn.App.job.entity.Job;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    @Query("""
    SELECT j FROM Job j 
    WHERE (LOWER(j.companyName) IN :companies OR LOWER(j.location) = :location OR j.experience >= :experience)
    AND j.id NOT IN :excludedJobIds
    ORDER BY j.createdAt DESC
    """)
    List<Job> findRecommendedJobs(@Param("companies") Set<String> companies,
                                  @Param("location") String location,
                                  @Param("experience") double experience,
                                  @Param("excludedJobIds") Set<Long> excludedJobIds);

}
