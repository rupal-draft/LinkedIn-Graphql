package com.linkedIn.LinkedIn.App.post.resolver;

import com.linkedIn.LinkedIn.App.common.dto.Response;
import com.linkedIn.LinkedIn.App.post.dto.CommentDto;
import com.linkedIn.LinkedIn.App.post.dto.CommentResponseDto;
import com.linkedIn.LinkedIn.App.post.dto.LikeDto;
import com.linkedIn.LinkedIn.App.post.dto.LikeResponseDto;
import com.linkedIn.LinkedIn.App.post.service.CommentService;
import com.linkedIn.LinkedIn.App.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class LikeCommentController {

    private final LikeService likeService;
    private final CommentService commentService;

    // Likes

    @MutationMapping
    public Response likePost(@Argument Long postId){
        likeService.likePost(postId);
        return new Response("Post liked successfully", true);
    }

    @MutationMapping
    public Response unlikePost(@Argument Long postId){
        likeService.unlikePost(postId);
        return new Response("Post unliked successfully", true);
    }

    @QueryMapping
    public LikeResponseDto getAllLikes(@Argument Long postId){
        Set<LikeDto> likes = likeService.getAllLikes(postId);
        return new LikeResponseDto("Likes fetched successfully", true, likes.size(), likes);
    }

    // Comments

    @MutationMapping
    public Response addComment(@Argument Long postId, @Argument String content){
        commentService.addComment(postId, content);
        return new Response("Comment added successfully", true);
    }

    @MutationMapping
    public Response deleteComment(@Argument Long postId, @Argument Long commentId){
        commentService.deleteComment(postId, commentId);
        return new Response("Comment deleted successfully", true);
    }

    @MutationMapping
    public Response updateComment(@Argument Long commentId, @Argument String content){
        commentService.updateComment(commentId, content);
        return new Response("Comment updated successfully", true);
    }

    @QueryMapping
    public CommentResponseDto getAllComments(@Argument Long postId){
        Set<CommentDto> comments = commentService.getAllComments(postId);
        return new CommentResponseDto("Comments fetched successfully", true, comments.size(), comments);
    }
}
