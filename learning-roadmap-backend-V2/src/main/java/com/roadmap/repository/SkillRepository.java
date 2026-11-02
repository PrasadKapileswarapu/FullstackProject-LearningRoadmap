package com.roadmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.roadmap.model.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByRoleIdOrderByPriorityAsc(Long roleId);
}
