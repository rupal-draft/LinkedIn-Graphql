package com.linkedIn.LinkedIn.App.post.service.implementations;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.ResourceNotFoundException;
import com.linkedIn.LinkedIn.App.post.dto.LikeDto;
import com.linkedIn.LinkedIn.App.post.entity.Like;
import com.linkedIn.LinkedIn.App.post.entity.Post;
import com.linkedIn.LinkedIn.App.post.repository.LikeRepository;
import com.linkedIn.LinkedIn.App.post.repository.PostRepository;
import com.linkedIn.LinkedIn.App.post.service.LikeService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final ModelMapper modelMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    @CacheEvict(value = "likes", key = "#postId")
    public void likePost(Long postId) {
        log.info("Attempting to like post with ID: {}", postId);

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post not found with ID: {}", postId);
                        return new ResourceNotFoundException("Post not found with ID: " + postId);
                    });

            User user = SecurityUtils.getLoggedInUser();

            boolean alreadyLiked = post.getLikes().stream()
                    .anyMatch(like -> like.getUser().getId().equals(user.getId()));

            if (alreadyLiked) {
                log.info("User {} already liked post {}", user.getId(), postId);
                return;
            }

            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();

            post.getLikes().add(like);
            likeRepository.save(like);

            log.info("User {} liked post {}", user.getId(), postId);

        } catch (Exception ex) {
            log.error("Error while liking post with ID {}: {}", postId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to like post. Please try again later.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "likes", key = "#postId")
    public void unlikePost(Long postId) {
        log.info("Attempting to unlike post with ID: {}", postId);

        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> {
                        log.warn("Post not found with ID: {}", postId);
                        return new ResourceNotFoundException("Post not found with ID: " + postId);
                    });

            User user = SecurityUtils.getLoggedInUser();

            Optional<Like> existingLike = post.getLikes().stream()
                    .filter(like -> like.getUser().getId().equals(user.getId()))
                    .findFirst();

            if (existingLike.isEmpty()) {
                log.info("User {} has not liked post {}", user.getId(), postId);
                return;
            }

            post.getLikes().remove(existingLike.get());

            likeRepository.delete(existingLike.get());

            log.info("User {} unliked post {}", user.getId(), postId);

        } catch (Exception ex) {
            log.error("Error while unliking post with ID {}: {}", postId, ex.getMessage(), ex);
            throw new RuntimeException("Failed to unlike post. Please try again later.");
        }
    }

    @Override
    @Cacheable(value = "likes", key = "#postId")
    public Set<LikeDto> getAllLikes(Long postId) {
        log.info("Fetching likes for post with ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);
                    return new ResourceNotFoundException("Post not found with ID: " + postId);
                });

        try {
            Set<LikeDto> likeDtos = post.getLikes().stream()
                    .map(like -> modelMapper.map(like, LikeDto.class))
                    .collect(Collectors.toSet());

            log.info("Fetched {} likes for post ID: {}", likeDtos.size(), postId);
            return likeDtos;
        } catch (MappingException e) {
            log.error("Error mapping Like entities to DTOs for post ID {}: {}", postId, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch likes for the post.");
        }
    }
}
