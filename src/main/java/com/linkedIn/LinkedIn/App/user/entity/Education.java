package com.linkedIn.LinkedIn.App.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "education",indexes = {
        @Index(columnList = "user_id",name = "user_id_idx"),
        @Index(columnList = "field",name = "field_idx"),
        @Index(columnList = "degree",name = "degree_idx")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "School is required")
    private String school;

    @NotBlank(message = "Degree is required")
    private String degree;

    @NotBlank(message = "Field of study is required")
    private String field;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 10, message = "Grade too long")
    private String grade;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
