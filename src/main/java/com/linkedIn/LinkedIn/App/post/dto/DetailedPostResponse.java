package com.linkedIn.LinkedIn.App.post.dto;

import com.linkedIn.LinkedIn.App.post.entity.enums.Category;
import com.linkedIn.LinkedIn.App.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedPostResponse {

    private Long id;
    private String content;
    private Category category;
    private String imageUrl;
    private UserDto user;
    private int likesCount;
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
}
