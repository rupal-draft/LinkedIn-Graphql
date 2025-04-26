package com.linkedIn.LinkedIn.App.job.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobStatus;
import com.linkedIn.LinkedIn.App.job.entity.enums.JobType;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(name = "idx_job_title", columnList = "title"),
                @Index(name = "idx_job_location", columnList = "location"),
                @Index(name = "idx_job_company", columnList = "companyName"),
                @Index(name = "idx_job_type", columnList = "jobType"),
                @Index(name = "idx_job_status", columnList = "status"),
                @Index(name = "idx_job_posted_by", columnList = "posted_by_user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 100)
    private String location;

    @Column(nullable = false)
    private Double salary;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "posted_by_user_id", nullable = false)
    private User postedBy;
}
