package com.roadmap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.roadmap.model.UserRoadmap;


@Repository
public interface UserRoadmapRepository extends JpaRepository<UserRoadmap, Long> {


    List<UserRoadmap> findByUserEmailOrderByCreatedAtDesc(String userEmail);

    Optional<UserRoadmap> findTopByUserEmailAndTargetRoleOrderByCreatedAtDesc(
            String userEmail, String targetRole
    );
}
