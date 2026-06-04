package com.roadmap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.roadmap.dto.RoadmapRequest;
import com.roadmap.dto.RoadmapResponse;
import com.roadmap.model.Role;
import com.roadmap.model.Skill;
import com.roadmap.model.UserRoadmap;
import com.roadmap.repository.RoleRepository;
import com.roadmap.repository.SkillRepository;
import com.roadmap.repository.UserRoadmapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final UserRoadmapRepository userRoadmapRepository;


    public RoadmapResponse generateRoadmap(RoadmapRequest request) {
    	System.out.println("Generate roadmap API called");
    	System.out.println(request);
        Role targetRole = roleRepository.findById(request.getTargetRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + request.getTargetRoleId()));

        List<Skill> allRequiredSkills = skillRepository.findByRoleIdOrderByPriorityAsc(targetRole.getId());


        List<String> userSkillsLower = new ArrayList<>();
        if (request.getCurrentSkills() != null) {
            userSkillsLower = request.getCurrentSkills()
                    .stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }


        List<Skill> matchedSkills = new ArrayList<>();
        List<Skill> missingSkills = new ArrayList<>();

        for (Skill skill : allRequiredSkills) {
            if (userSkillsLower.contains(skill.getName().toLowerCase())) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }


        int totalHours = missingSkills.stream()
                .mapToInt(Skill::getEstimatedHours)
                .sum();


        int totalSkills = allRequiredSkills.size();
        int completionPercentage = totalSkills == 0 ? 0 :
                (int) Math.round((matchedSkills.size() * 100.0) / totalSkills);


        UserRoadmap userRoadmap = new UserRoadmap();
        userRoadmap.setUserName(request.getUserName());
        userRoadmap.setUserEmail(request.getUserEmail());
        userRoadmap.setTargetRole(targetRole.getName());
        userRoadmap.setCurrentSkills(
                request.getCurrentSkills() != null ?
                String.join(", ", request.getCurrentSkills()) : ""
        );
        userRoadmap.setSkillsToLearn(
                missingSkills.stream().map(Skill::getName).collect(Collectors.joining(", "))
        );
        userRoadmap.setTotalEstimatedHours(totalHours);
        userRoadmap.setCompletionPercentage(completionPercentage);
        System.out.println("Before save");
        UserRoadmap savedRoadmap = userRoadmapRepository.save(userRoadmap);
        System.out.println("After save");
        System.out.println("Saved ID = " + savedRoadmap.getId());

        return RoadmapResponse.builder()
                .roadmapId(savedRoadmap.getId())
                .userName(request.getUserName())
                .userEmail(request.getUserEmail())
                .targetRole(targetRole.getName())
                .roleCategory(targetRole.getCategory())
                .roleDescription(targetRole.getDescription())
                .matchedSkills(convertToSkillDTOs(matchedSkills, true))
                .skillsToLearn(convertToSkillDTOs(missingSkills, false))
                .totalSkillsRequired(totalSkills)
                .skillsAlreadyKnown(matchedSkills.size())
                .skillsToLearnCount(missingSkills.size())
                .completionPercentage(completionPercentage)
                .totalHoursToLearn(totalHours)
                .generatedAt(savedRoadmap.getCreatedAt())
                .build();
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    public List<UserRoadmap> getUserHistory(String email) {
        return userRoadmapRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }


    private List<RoadmapResponse.SkillDTO> convertToSkillDTOs(List<Skill> skills, boolean alreadyKnown) {
        return skills.stream()
                .map(skill -> RoadmapResponse.SkillDTO.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .category(skill.getCategory())
                        .priority(skill.getPriority())
                        .priorityLabel(getPriorityLabel(skill.getPriority()))
                        .description(skill.getDescription())
                        .estimatedHours(skill.getEstimatedHours())
                        .resourceUrl(skill.getResourceUrl())
                        .alreadyKnown(alreadyKnown)
                        .build())
                .collect(Collectors.toList());
    }


    private String getPriorityLabel(Integer priority) {
        return switch (priority) {
            case 1 -> "Must Know";
            case 2 -> "Should Know";
            case 3 -> "Good to Know";
            default -> "Optional";
        };
    }
}
