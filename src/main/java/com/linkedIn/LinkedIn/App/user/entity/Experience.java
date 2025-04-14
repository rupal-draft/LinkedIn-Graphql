package com.linkedIn.LinkedIn.App.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "experiences", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_company", columnList = "company"),
    @Index(name = "idx_years_of_experience", columnList = "years_of_experience")
})
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

    @Column(name = "years_of_experience")
    private double yearsOfExperience;

    @Size(max = 1000, message = "Description too long")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Experience() {
    }

    public Experience(Long id, String company, String title, String location, LocalDate fromDate, LocalDate toDate, boolean currentlyWorking, double yearsOfExperience, String description, User user) {
        this.id = id;
        this.company = company;
        this.title = title;
        this.location = location;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.currentlyWorking = currentlyWorking;
        this.yearsOfExperience = yearsOfExperience;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Company is required") String getCompany() {
        return company;
    }

    public void setCompany(@NotBlank(message = "Company is required") String company) {
        this.company = company;
    }

    public @NotBlank(message = "Title is required") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title is required") String title) {
        this.title = title;
    }

    public @NotBlank(message = "Location is required") String getLocation() {
        return location;
    }

    public void setLocation(@NotBlank(message = "Location is required") String location) {
        this.location = location;
    }

    public @NotNull(message = "From date is required") LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(@NotNull(message = "From date is required") LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public @NotNull(message = "To date is required") LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(@NotNull(message = "To date is required") LocalDate toDate) {
        this.toDate = toDate;
    }

    public boolean isCurrentlyWorking() {
        return currentlyWorking;
    }

    public void setCurrentlyWorking(boolean currentlyWorking) {
        this.currentlyWorking = currentlyWorking;
    }

    public double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
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
