package com.linkedIn.LinkedIn.App.post.service;

import com.linkedIn.LinkedIn.App.post.dto.DetailedPostResponse;
import com.linkedIn.LinkedIn.App.post.dto.PostResponse;
import com.linkedIn.LinkedIn.App.post.dto.records.PostInput;
import com.linkedIn.LinkedIn.App.post.entity.Post;
import jakarta.validation.Valid;

import java.util.List;

public interface PostService {

    PostResponse createPost(@Valid PostInput postInput);

    void updatePost(Long id, String content);

    void deletePost(Long id);

    void changeVisibility(Long id, String visibility);

    List<DetailedPostResponse> getAllPosts();

    List<DetailedPostResponse> getPostsByCategory(String category);

    List<DetailedPostResponse> getMyPosts();

    List<DetailedPostResponse> getPostsByUser(Long userId);

    List<DetailedPostResponse> mapToDetailedPostResponse(List<Post> posts);

    DetailedPostResponse getPostById(Long id);
}
