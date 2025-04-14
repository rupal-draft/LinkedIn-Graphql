package com.linkedIn.LinkedIn.App.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "experiences", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_company", columnList = "company"),
    @Index(name = "idx_years_of_experience", columnList = "years_of_experience")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    @Column(name = "currently_working")
    private boolean currentlyWorking;

    @Column(name = "years_of_experience", precision = 2, scale = 1)
    private double yearsOfExperience;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
