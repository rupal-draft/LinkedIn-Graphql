package com.linkedIn.LinkedIn.App.user.repository;

import com.linkedIn.LinkedIn.App.user.entity.Education;
import com.linkedIn.LinkedIn.App.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    @Query("SELECT DISTINCT e.user FROM Education e WHERE e.field = :field OR e.degree = :degree")
    List<User> findUsersByEducationFieldOrDegree(@Param("field") String field, @Param("degree") String degree);

}
