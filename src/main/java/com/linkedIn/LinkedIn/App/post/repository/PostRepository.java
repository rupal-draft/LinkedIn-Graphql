package com.linkedIn.LinkedIn.App.post.repository;

import com.linkedIn.LinkedIn.App.post.entity.Post;
import com.linkedIn.LinkedIn.App.post.entity.enums.Category;
import com.linkedIn.LinkedIn.App.post.entity.enums.Visibility;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUser(User user);

    List<Post> findByCategory(Category category);

    @EntityGraph(value = "Post.withLikesAndCommentsAndUser")
    List<Post> findAll();

    @Modifying
    @Query("UPDATE Post p SET p.content = ?2 WHERE p.id = ?1")
    int updatePost(Long id, String content);


    @Modifying
    @Query("UPDATE Post p SET p.deleted = true WHERE p.id = ?1")
    int deletePost(Long id);

    @Modifying
    @Query("UPDATE Post p SET p.visibility = ?2 WHERE p.id = ?1")
    int updateVisibility(Long id, Visibility visibility);
}
