package com.roadmap.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RoadmapRequest {


    @NotBlank(message = "Name is required")
    private String userName;


    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String userEmail;


    @NotNull(message = "Target role ID is required")
    private Long targetRoleId;


    private List<String> currentSkills;
}
