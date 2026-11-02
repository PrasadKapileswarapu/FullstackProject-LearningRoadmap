package com.roadmap.dto;

import com.roadmap.model.AppUser;

public record UserResponse(Long id, String name, String email) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail());
    }
}
