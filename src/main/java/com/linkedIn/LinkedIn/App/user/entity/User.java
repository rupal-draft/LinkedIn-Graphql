package com.linkedIn.LinkedIn.App.user.entity;


import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users",indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_location", columnList = "location"),
        @Index(name = "idx_user_role", columnList = "role")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

}
