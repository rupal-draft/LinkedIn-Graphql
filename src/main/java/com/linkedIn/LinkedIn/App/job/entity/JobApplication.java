package com.linkedIn.LinkedIn.App.job.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.job.entity.enums.ApplicationStatus;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "job_id"})
        },
        indexes = {
                @Index(name = "idx_job_id", columnList = "job_id"),
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_application_status", columnList = "application_status")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "resume_url")
    private String resumeUrl;

    @Column(nullable = false, name = "application_status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus;
}

