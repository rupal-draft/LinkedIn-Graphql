package com.linkedIn.LinkedIn.App.post.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.post.dto.*;
import com.linkedIn.LinkedIn.App.post.dto.records.PostInput;
import com.linkedIn.LinkedIn.App.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PostResolver {

    private final PostService postService;

    public PostResolver(PostService postService) {
        this.postService = postService;
    }

    @MutationMapping
    public CreatedPostResponse createPost(@Argument("postInput") @Valid PostInput postInput) {
        PostResponse postResponse = postService.createPost(postInput);
        return new CreatedPostResponse("Post created successfully", true, postResponse);
    }

    @MutationMapping
    public Response updatePost(@Argument Long id, @Argument String content) {
        postService.updatePost(id, content);
        return new Response("Post updated successfully", true);
    }

    @MutationMapping
    public Response deletePost(@Argument Long id) {
        postService.deletePost(id);
        return new Response("Post deleted successfully", true);
    }

    @MutationMapping
    public Response changeVisibility(@Argument Long id, @Argument String visibility) {
        postService.changeVisibility(id, visibility);
        return new Response("Post visibility changed successfully", true);
    }

    @QueryMapping
    public MultiplePostsResponse getAllPosts() {
        List<DetailedPostResponse> posts = postService.getAllPosts();
        return new MultiplePostsResponse("Posts fetched successfully", true, posts.size(), posts);
    }

    @QueryMapping
    public MultiplePostsResponse getMyPosts() {
        List<DetailedPostResponse> posts = postService.getMyPosts();
        return new MultiplePostsResponse("Posts fetched successfully", true, posts.size(), posts);
    }

    @QueryMapping
    public MultiplePostsResponse getPostsByUser(@Argument Long userId) {
        List<DetailedPostResponse> posts = postService.getPostsByUser(userId);
        return new MultiplePostsResponse("Posts fetched successfully", true, posts.size(), posts);
    }

    @QueryMapping
    public MultiplePostsResponse getPostsByCategory(@Argument String category) {
        List<DetailedPostResponse> posts = postService.getPostsByCategory(category);
        return new MultiplePostsResponse("Posts fetched successfully", true, posts.size(), posts);
    }

    @QueryMapping
    public SinglePostResponse getPostById(@Argument Long id) {
        DetailedPostResponse post = postService.getPostById(id);
        return new SinglePostResponse("Post fetched successfully", true, post);
    }
}
