package com.linkedIn.LinkedIn.App.post.service.implementations;

import com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils;
import com.linkedIn.LinkedIn.App.common.exceptions.BadRequestException;
import com.linkedIn.LinkedIn.App.common.exceptions.ServiceUnavailableException;
import com.linkedIn.LinkedIn.App.connections.repository.ConnectionRequestRepository;
import com.linkedIn.LinkedIn.App.connections.service.ConnectionService;
import com.linkedIn.LinkedIn.App.notification.entity.enums.NotificationType;
import com.linkedIn.LinkedIn.App.notification.service.NotificationService;
import com.linkedIn.LinkedIn.App.post.dto.DetailedPostResponse;
import com.linkedIn.LinkedIn.App.post.dto.PostResponse;
import com.linkedIn.LinkedIn.App.post.dto.records.PostInput;
import com.linkedIn.LinkedIn.App.post.entity.Post;
import com.linkedIn.LinkedIn.App.post.entity.enums.Category;
import com.linkedIn.LinkedIn.App.post.entity.enums.Visibility;
import com.linkedIn.LinkedIn.App.post.repository.PostRepository;
import com.linkedIn.LinkedIn.App.post.service.PostService;
import com.linkedIn.LinkedIn.App.user.entity.User;
import com.linkedIn.LinkedIn.App.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final UserRepository userRepository;

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public PostResponse createPost(PostInput postInput) {
        try {
            User user = SecurityUtils.getLoggedInUser();
            log.info("Creating post for user: {}", user.getName());
            Post post = modelMapper.map(postInput, Post.class);
            post.setCategory(Category.valueOf(postInput.category().toUpperCase()));
            post.setUser(user);

            Post savedPost = postRepository.save(post);

            Set<User> connections = user.getConnections();
            log.info("Sending notifications to {} connections", connections.size());
            connections.forEach(connection -> {
                notificationService.createNotification(
                        "New post from " + user.getName(),
                        "Check out " + savedPost.getContent(),
                        connection,
                        NotificationType.POST);
            });

            return modelMapper.map(savedPost, PostResponse.class);

        } catch (MappingException e) {
            log.error("Failed to map PostInput to Post entity", e);
            throw new BadRequestException("Invalid post data");

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violated while saving post", e);
            throw new RuntimeException("Post content or fields invalid or duplicate");

        } catch (Exception e) {
            log.error("Unexpected error while creating post", e);
            throw new ServiceUnavailableException("Could not create post at the moment");
        }
    }


    @Override
    @Transactional
    @CacheEvict(value = "posts", key = "#id")
    public void updatePost(Long id, String content) {
        SecurityUtils.checkUserLoggedIn();
        try {
            log.info("Updating post with id: {}", id);
            int updated = postRepository.updatePost(id, content);
            if (updated == 0) {
                log.warn("No post found with id: {}", id);
                throw new EntityNotFoundException("Post not found with id: " + id);
            }
            log.info("Updated post with id: {}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation while updating post", e);
            throw new IllegalArgumentException("Invalid post content or input");
        } catch (Exception e) {
            log.error("Failed to update post", e);
            throw new RuntimeException("Unexpected error occurred while updating post");
        }
    }


    @Override
    @CacheEvict(value = "posts", key = "#id")
    @Transactional
    public void deletePost(Long id) {
        SecurityUtils.checkUserLoggedIn();
        try {
            log.info("Deleting post with id: {}", id);
            int deleted = postRepository.deletePost(id);
            if (deleted == 0) {
                log.warn("No post to update found with id: {}", id);
                throw new EntityNotFoundException("Post not found with id: " + id);
            }
            log.info("Deleted post with id: {}", id);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting post", e);
            throw new RuntimeException("Unexpected error occurred while deleting post");
        }
    }

    @Override
    @CacheEvict(value = "posts", key = "#id")
    @Transactional
    public void changeVisibility(Long id, String visibility) {
        SecurityUtils.checkUserLoggedIn();
        try {
            log.info("Changing visibility of post with id: {}", id);
            int updated = postRepository.updateVisibility(id, Visibility.valueOf(visibility));
            if (updated == 0) {
                log.warn("No post to delete found with id: {}", id);
                throw new EntityNotFoundException("Post not found with id: " + id);
            }
            log.info("Changed visibility of post with id: {}", id);
        } catch (Exception e) {
            log.error("Unexpected error occurred while changing visibility of post", e);
            throw new RuntimeException("Unexpected error occurred while changing visibility of post");
        }
    }

    @Override
    @Cacheable(value = "posts", key = "#userId")
    public List<DetailedPostResponse> getPostsByUser(Long userId) {
        log.info("Getting posts for user with id: {}", userId);
        User user = userRepository
                .findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        List<Post> posts = postRepository.findByUser(user);
        log.info("Found {} posts for user with id: {}", posts.size(), userId);
        return mapToDetailedPostResponse(posts);
    }

    @Override
    @Cacheable(value = "posts", key = "#root.methodName + '_' + T(com.linkedIn.LinkedIn.App.auth.utils.SecurityUtils).getLoggedInUser().getId()")
    public List<DetailedPostResponse> getMyPosts() {
        log.info("Getting my posts");
        User user = SecurityUtils.getLoggedInUser();
        List<Post> posts = postRepository.findByUser(user);
        log.info("Found my {} posts", posts.size());
        return mapToDetailedPostResponse(posts);
    }

    @Override
    @Cacheable(value = "posts", key = "#category")
    public List<DetailedPostResponse> getPostsByCategory(String category) {
        log.info("Getting posts for category: {}", category);
        List<Post> posts = postRepository.findByCategory(Category.valueOf(category));
        log.info("Found {} posts for category: {}", posts.size(), category);
        return mapToDetailedPostResponse(posts);
    }

    @Override
    @Cacheable(value = "posts")
    public List<DetailedPostResponse> getAllPosts() {
        log.info("Getting all posts");
        List<Post> posts = postRepository.findAll();
        log.info("Found {} posts", posts.size());
        return mapToDetailedPostResponse(posts);
    }

    @Override
    public List<DetailedPostResponse> mapToDetailedPostResponse(List<Post> posts) {
        return posts.stream().map(post -> {
            try{
                DetailedPostResponse response = modelMapper.map(post, DetailedPostResponse.class);
                response.setLikesCount(post.getLikes().size());
                return response;
            } catch (MappingException e) {
                log.error("Failed to map Post entity to DetailedPostResponse", e);
                throw new RuntimeException("Failed to map Post entity to DetailedPostResponse");
            }
        }).toList();
    }

    @Override
    @Cacheable(value = "posts", key = "#id")
    public DetailedPostResponse getPostById(Long id) {
        log.info("Getting post with id: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        log.info("Found post with id: {}", id);
        try {
            DetailedPostResponse response = modelMapper.map(post, DetailedPostResponse.class);
            response.setLikesCount(post.getLikes().size());
            return response;
        } catch (MappingException e) {
            log.error("Failed to map Post entity to DetailedPostResponse", e);
            throw new RuntimeException("Failed to map Post entity to DetailedPostResponse");
        }
    }
}
