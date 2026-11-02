package com.roadmap.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapResponse {

    private Long roadmapId;
    private String userName;
    private String userEmail;
    private String targetRole;
    private String roleCategory;
    private String roleDescription;

    private List<SkillDTO> matchedSkills;
    private List<SkillDTO> skillsToLearn;

    private Integer totalSkillsRequired;
    private Integer skillsAlreadyKnown;
    private Integer skillsToLearnCount;
    private Integer completionPercentage;
    private Integer totalHoursToLearn;

    private LocalDateTime generatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDTO {
        private Long id;
        private String name;
        private String category;
        private Integer priority;
        private String priorityLabel;
        private String description;
        private Integer estimatedHours;
        private String resourceUrl;
        private boolean alreadyKnown;
    }
}
