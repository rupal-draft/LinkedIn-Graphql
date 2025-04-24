package com.linkedIn.LinkedIn.App.user.repository;

import com.linkedIn.LinkedIn.App.user.entity.Experience;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    @Query("SELECT DISTINCT e.user FROM Experience e WHERE e.position = :position OR e.yearsOfExperience >= :years")
    List<User> findUsersByPositionOrExperience(@Param("position") String position, @Param("years") double years);
}
