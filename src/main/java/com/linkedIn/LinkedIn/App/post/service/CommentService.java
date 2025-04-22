package com.linkedIn.LinkedIn.App.post.service;

import com.linkedIn.LinkedIn.App.post.dto.CommentDto;

import java.util.Set;

public interface CommentService {

    void addComment(Long postId, String content);

    void deleteComment(Long postId, Long commentId);

    void updateComment(Long commentId, String content);

    Set<CommentDto> getAllComments(Long postId);
}
