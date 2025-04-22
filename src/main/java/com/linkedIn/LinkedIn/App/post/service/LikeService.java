package com.linkedIn.LinkedIn.App.post.service;

import com.linkedIn.LinkedIn.App.post.dto.LikeDto;

import java.util.Set;

public interface LikeService {

    void likePost(Long postId);

    void unlikePost(Long postId);

    Set<LikeDto> getAllLikes(Long postId);
}
