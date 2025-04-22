package com.linkedIn.LinkedIn.App.post.repository;

import com.linkedIn.LinkedIn.App.post.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
}
