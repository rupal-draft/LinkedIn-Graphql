package com.linkedIn.LinkedIn.App.user.dto;

import com.linkedIn.LinkedIn.App.user.entity.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private Roles role;

}
