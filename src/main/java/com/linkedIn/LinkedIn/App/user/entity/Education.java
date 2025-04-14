package com.linkedIn.LinkedIn.App.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


@Entity
@Table(name = "education",indexes = {
        @Index(columnList = "user_id",name = "user_id_idx"),
        @Index(columnList = "field",name = "field_idx"),
        @Index(columnList = "degree",name = "degree_idx")
})
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

    public Education() {
    }

    public Education(Long id, String school, String degree, String field, LocalDate startDate, LocalDate endDate, String grade, String description, User user) {
        this.id = id;
        this.school = school;
        this.degree = degree;
        this.field = field;
        this.startDate = startDate;
        this.endDate = endDate;
        this.grade = grade;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "School is required") String getSchool() {
        return school;
    }

    public void setSchool(@NotBlank(message = "School is required") String school) {
        this.school = school;
    }

    public @NotBlank(message = "Degree is required") String getDegree() {
        return degree;
    }

    public void setDegree(@NotBlank(message = "Degree is required") String degree) {
        this.degree = degree;
    }

    public @NotBlank(message = "Field of study is required") String getField() {
        return field;
    }

    public void setField(@NotBlank(message = "Field of study is required") String field) {
        this.field = field;
    }

    public @NotNull(message = "Start date is required") LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull(message = "Start date is required") LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public @Size(max = 10, message = "Grade too long") String getGrade() {
        return grade;
    }

    public void setGrade(@Size(max = 10, message = "Grade too long") String grade) {
        this.grade = grade;
    }

    public @Size(max = 1000, message = "Description too long") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 1000, message = "Description too long") String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
