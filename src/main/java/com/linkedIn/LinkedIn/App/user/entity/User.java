package com.linkedIn.LinkedIn.App.user.entity;


import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_location", columnList = "location"),
        @Index(name = "idx_user_role", columnList = "role")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 100, message = "Headline can be up to 100 characters")
    private String headline;

    @Size(max = 1000, message = "About section is too long")
    private String about;

    @URL(message = "Invalid profile picture URL")
    private String profilePicture;

    @URL(message = "Invalid cover picture URL")
    private String coverPicture;

    @Size(max = 100, message = "Location can be up to 100 characters")
    private String location;

    @URL(message = "Invalid website URL")
    private String website;

    @Pattern(
            regexp = "^\\+?[0-9. ()-]{7,25}$",
            message = "Invalid phone number"
    )
    private String phone;

    @Size(max = 100, message = "Current position is too long")
    private String currentPosition;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToMany
    private Set<User> connections;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experienceList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList;


    public User() {
    }

    public User(Long id, String name, String email, String password, String headline, String about, String profilePicture, String coverPicture, String location, String website, String phone, String currentPosition, Roles role, Set<User> connections, List<Experience> experienceList, List<Education> educationList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.headline = headline;
        this.about = about;
        this.profilePicture = profilePicture;
        this.coverPicture = coverPicture;
        this.location = location;
        this.website = website;
        this.phone = phone;
        this.currentPosition = currentPosition;
        this.role = role;
        this.connections = connections;
        this.experienceList = experienceList;
        this.educationList = educationList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters") String name) {
        this.name = name;
    }

    public @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password) {
        this.password = password;
    }

    public @Size(max = 100, message = "Headline can be up to 100 characters") String getHeadline() {
        return headline;
    }

    public void setHeadline(@Size(max = 100, message = "Headline can be up to 100 characters") String headline) {
        this.headline = headline;
    }

    public @Size(max = 1000, message = "About section is too long") String getAbout() {
        return about;
    }

    public void setAbout(@Size(max = 1000, message = "About section is too long") String about) {
        this.about = about;
    }

    public @URL(message = "Invalid profile picture URL") String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(@URL(message = "Invalid profile picture URL") String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public @URL(message = "Invalid cover picture URL") String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(@URL(message = "Invalid cover picture URL") String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public @Size(max = 100, message = "Location can be up to 100 characters") String getLocation() {
        return location;
    }

    public void setLocation(@Size(max = 100, message = "Location can be up to 100 characters") String location) {
        this.location = location;
    }

    public @URL(message = "Invalid website URL") String getWebsite() {
        return website;
    }

    public void setWebsite(@URL(message = "Invalid website URL") String website) {
        this.website = website;
    }

    public @Pattern(
            regexp = "^\\+?[0-9. ()-]{7,25}$",
            message = "Invalid phone number"
    ) String getPhone() {
        return phone;
    }

    public void setPhone(@Pattern(
            regexp = "^\\+?[0-9. ()-]{7,25}$",
            message = "Invalid phone number"
    ) String phone) {
        this.phone = phone;
    }

    public @Size(max = 100, message = "Current position is too long") String getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(@Size(max = 100, message = "Current position is too long") String currentPosition) {
        this.currentPosition = currentPosition;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Set<User> getConnections() {
        return connections;
    }

    public void setConnections(Set<User> connections) {
        this.connections = connections;
    }

    public List<Experience> getExperienceList() {
        return experienceList;
    }

    public void setExperienceList(List<Experience> experienceList) {
        this.experienceList = experienceList;
    }

    public List<Education> getEducationList() {
        return educationList;
    }

    public void setEducationList(List<Education> educationList) {
        this.educationList = educationList;
    }
}
