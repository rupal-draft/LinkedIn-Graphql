package com.linkedIn.LinkedIn.App.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseDto {
    private String message;
    private boolean success;
    private int likesCount;
    private Set<LikeDto> likes;
}
