package com.linkedIn.LinkedIn.App.post.dto.records;

import com.linkedIn.LinkedIn.App.post.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private String message;
    private boolean success;
    private int commentsCount;
    private CommentDto comment;
}
