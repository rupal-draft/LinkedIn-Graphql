package com.linkedIn.LinkedIn.App.auth.dto;

import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;

public class UserDto {

    private Long id;
    private String email;
    private Roles role;

    public UserDto() {
    }

    public UserDto(Long id, String email, Roles role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }
}
