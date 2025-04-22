package com.linkedIn.LinkedIn.App.post.service.implementations;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.post.dto.CommentDto;
import com.linkedIn.LinkedIn.App.post.entity.Comment;
import com.linkedIn.LinkedIn.App.post.entity.Post;
import com.linkedIn.LinkedIn.App.post.repository.CommentRepository;
import com.linkedIn.LinkedIn.App.post.repository.PostRepository;
import com.linkedIn.LinkedIn.App.post.service.CommentService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    @CacheEvict(value = "comments", key = "#postId")
    public void addComment(Long postId, String content) {
        log.info("Attempting to add comment to post with ID: {}", postId);

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post not found with ID: {}", postId);
                        return new ResourceNotFoundException("Post not found with ID: " + postId);
                    });

            User user = SecurityUtils.getLoggedInUser();

            Comment comment = Comment.builder()
                    .user(user)
                    .post(post)
                    .content(content)
                    .build();

            commentRepository.save(comment);

            log.info("Comment added successfully by user {} on post {}", user.getId(), postId);
        } catch (Exception e) {
            log.error("Failed to add comment to post with ID: {}. Reason: {}", postId, e.getMessage(), e);
            throw new RuntimeException("Failed to add comment to post. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "comments", key = "#postId")
    public void deleteComment(Long postId, Long commentId) {
        log.info("Attempting to delete comment with ID: {} from post with ID: {}", commentId, postId);

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post not found with ID: {}", postId);
                        return new ResourceNotFoundException("Post not found with ID: " + postId);
                    });

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> {
                        log.warn("Comment not found with ID: {}", commentId);
                        return new ResourceNotFoundException("Comment not found with ID: " + commentId);
                    });

            if (!comment.getPost().getId().equals(postId)) {
                log.warn("Comment with ID: {} does not belong to post with ID: {}", commentId, postId);
                throw new IllegalArgumentException("Comment does not belong to the specified post");
            }

            post.getComments().remove(comment);

            commentRepository.delete(comment);
            log.info("Comment with ID: {} deleted successfully from post with ID: {}", commentId, postId);

        } catch (Exception e) {
            log.error("Failed to delete comment with ID: {} from post with ID: {}. Reason: {}", commentId, postId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete comment. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "comments")
    public void updateComment(Long commentId, String content) {
        User user = SecurityUtils.getLoggedInUser();
        log.info("User {} attempting to update comment with ID: {}", user.getId(), commentId);

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> {
                        log.warn("Comment not found with ID: {}", commentId);
                        return new ResourceNotFoundException("Comment not found with ID: " + commentId);
                    });

            if (!comment.getUser().getId().equals(user.getId())) {
                log.warn("Unauthorized update attempt by user {} on comment {}", user.getId(), commentId);
                throw new AccessDeniedException("You can only update your own comments");
            }

            commentRepository.updateComment(commentId, content);
            log.info("Comment with ID {} successfully updated by user {}", commentId, user.getId());

        } catch (Exception e) {
            log.error("Error updating comment {} by user {}: {}", commentId, user.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to update comment. Please try again.");
        }
    }

    @Override
    @Cacheable(value = "comments", key = "#postId")
    public Set<CommentDto> getAllComments(Long postId) {
        log.info("Fetching all comments for post with ID: {}", postId);

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post not found with ID: {}", postId);
                        return new ResourceNotFoundException("Post not found with ID: " + postId);
                    });

            Set<CommentDto> commentDtos = post.getComments().stream()
                    .map(comment -> {
                        try {
                            return modelMapper.map(comment, CommentDto.class);
                        } catch (MappingException e) {
                            log.error("Failed to map Comment entity to CommentDto for post {}", postId, e);
                            throw new RuntimeException("Failed to map Comment entity to CommentDto");
                        }
                    })
                    .collect(Collectors.toSet());

            log.info("Successfully fetched {} comments for post {}", commentDtos.size(), postId);
            return commentDtos;

        } catch (Exception e) {
            log.error("Error fetching comments for post {}: {}", postId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch comments for the post. Please try again later.");
        }
    }
}
