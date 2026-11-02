package com.roadmap.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

/** The user's name and email now come from the JWT, not from the request body. */
public record RoadmapRequest(
        @NotNull(message = "Target role ID is required")
        Long targetRoleId,

        List<String> currentSkills) {
}
