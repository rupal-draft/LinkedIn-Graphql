package com.linkedIn.LinkedIn.App.job.entity;

import com.linkedIn.LinkedIn.App.common.entity.Auditable;
import com.linkedIn.LinkedIn.App.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_jobs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "job_id"})
        },
        indexes = {
                @Index(name = "idx_saved_user_id", columnList = "user_id"),
                @Index(name = "idx_saved_job_id", columnList = "job_id")
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedJob extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}

