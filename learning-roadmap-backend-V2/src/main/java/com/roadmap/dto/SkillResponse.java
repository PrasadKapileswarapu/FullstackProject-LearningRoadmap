package com.roadmap.dto;

import com.roadmap.model.Skill;

public record SkillResponse(
        Long id,
        String name,
        String category,
        Integer priority,
        String priorityLabel,
        String description,
        Integer estimatedHours,
        String resourceUrl) {

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.getId(), skill.getName(), skill.getCategory(), skill.getPriority(),
                skill.priorityLabel(), skill.getDescription(), skill.getEstimatedHours(), skill.getResourceUrl());
    }
}
