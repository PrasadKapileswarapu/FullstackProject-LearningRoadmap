package com.roadmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.roadmap.model.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByRoleId(Long roleId);

    List<Skill> findByRoleIdOrderByPriorityAsc(Long roleId);

    @Query("SELECT s FROM Skill s WHERE s.role.id = :roleId AND LOWER(s.name) NOT IN :knownSkills")
    List<Skill> findMissingSkills(@Param("roleId") Long roleId,
                                   @Param("knownSkills") List<String> knownSkills);
}
