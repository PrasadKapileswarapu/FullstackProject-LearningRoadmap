package com.roadmap.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.roadmap.model.UserRoadmap;

/** One past roadmap, returned by GET /api/roadmap/history. */
public record RoadmapHistoryResponse(
        Long roadmapId,
        String targetRole,
        List<String> currentSkills,
        List<String> skillsToLearn,
        Integer totalEstimatedHours,
        Integer completionPercentage,
        LocalDateTime createdAt) {

    public static RoadmapHistoryResponse from(UserRoadmap roadmap) {
        return new RoadmapHistoryResponse(
                roadmap.getId(),
                roadmap.getTargetRole(),
                split(roadmap.getCurrentSkills()),
                split(roadmap.getSkillsToLearn()),
                roadmap.getTotalEstimatedHours(),
                roadmap.getCompletionPercentage(),
                roadmap.getCreatedAt());
    }

    private static List<String> split(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return List.of();
        }
        return Arrays.stream(commaSeparated.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
