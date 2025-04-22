package com.linkedIn.LinkedIn.App.post.repository;

import com.linkedIn.LinkedIn.App.post.entity.Comment;
import com.linkedIn.LinkedIn.App.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    @Modifying
    @Query("UPDATE Comment c SET c.content = ?2 WHERE c.id = ?1")
    void updateComment(Long id, String content);
}
