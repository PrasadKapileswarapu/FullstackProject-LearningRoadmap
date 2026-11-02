package com.roadmap.dto;

import com.roadmap.model.Role;

public record RoleResponse(Long id, String name, String category, String description) {

    public static RoleResponse from(Role role) {
        return new RoleResponse(role.getId(), role.getName(), role.getCategory(), role.getDescription());
    }
}
