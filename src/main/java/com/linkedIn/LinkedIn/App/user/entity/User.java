package com.linkedIn.LinkedIn.App.user.entity;


import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users",indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_location", columnList = "location"),
        @Index(name = "idx_user_role", columnList = "role")
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "headline", length = 100)
    private String headline;

    @Column(name = "about", length = 1000)
    private String about;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "cover_picture")
    private String coverPicture;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "website")
    private String website;

    @Column(name = "phone")
    private String phone;

    @Column(name = "current_position",length = 100)
    private String currentPosition;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToMany
    @JoinTable(
            name = "user_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private Set<User> connections = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Experience> experienceList;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Education> educationList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new
                SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
