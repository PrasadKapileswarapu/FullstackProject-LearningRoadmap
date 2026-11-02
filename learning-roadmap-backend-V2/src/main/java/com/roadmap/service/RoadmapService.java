package com.roadmap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadmap.dto.RoadmapHistoryResponse;
import com.roadmap.dto.RoadmapRequest;
import com.roadmap.dto.RoadmapResponse;
import com.roadmap.exception.ResourceNotFoundException;
import com.roadmap.model.AppUser;
import com.roadmap.model.Role;
import com.roadmap.model.Skill;
import com.roadmap.model.UserRoadmap;
import com.roadmap.repository.RoleRepository;
import com.roadmap.repository.SkillRepository;
import com.roadmap.repository.UserRepository;
import com.roadmap.repository.UserRoadmapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final UserRoadmapRepository userRoadmapRepository;

    /**
     * Compares the user's skills with what the target role needs and saves the result.
     * Runs in one transaction, so the roadmap is either fully saved or not saved at all.
     *
     * @param userEmail email of the authenticated user (taken from the JWT)
     */
    @Transactional
    public RoadmapResponse generateRoadmap(String userEmail, RoadmapRequest request) {
        AppUser user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Role targetRole = roleRepository.findById(request.targetRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.targetRoleId()));

        log.info("Generating roadmap for user id={} and role id={}", user.getId(), targetRole.getId());

        List<Skill> requiredSkills = skillRepository.findByRoleIdOrderByPriorityAsc(targetRole.getId());
        Set<String> knownSkills = SkillMatcher.normaliseAll(request.currentSkills());

        List<Skill> matchedSkills = new ArrayList<>();
        List<Skill> missingSkills = new ArrayList<>();
        for (Skill skill : requiredSkills) {
            if (SkillMatcher.isKnown(skill.getName(), knownSkills)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        int totalHours = missingSkills.stream().mapToInt(Skill::getEstimatedHours).sum();
        int totalSkills = requiredSkills.size();
        int completionPercentage = totalSkills == 0 ? 0
                : (int) Math.round((matchedSkills.size() * 100.0) / totalSkills);

        UserRoadmap roadmap = new UserRoadmap();
        roadmap.setUserName(user.getFullName());
        roadmap.setUserEmail(user.getEmail());
        roadmap.setTargetRole(targetRole.getName());
        roadmap.setCurrentSkills(request.currentSkills() == null ? ""
                : String.join(", ", request.currentSkills()));
        roadmap.setSkillsToLearn(missingSkills.stream().map(Skill::getName).collect(Collectors.joining(", ")));
        roadmap.setTotalEstimatedHours(totalHours);
        roadmap.setCompletionPercentage(completionPercentage);
        UserRoadmap saved = userRoadmapRepository.save(roadmap);

        log.debug("Saved roadmap id={}", saved.getId());

        return RoadmapResponse.builder()
                .roadmapId(saved.getId())
                .userName(user.getFullName())
                .userEmail(user.getEmail())
                .targetRole(targetRole.getName())
                .roleCategory(targetRole.getCategory())
                .roleDescription(targetRole.getDescription())
                .matchedSkills(toSkillDtos(matchedSkills, true))
                .skillsToLearn(toSkillDtos(missingSkills, false))
                .totalSkillsRequired(totalSkills)
                .skillsAlreadyKnown(matchedSkills.size())
                .skillsToLearnCount(missingSkills.size())
                .completionPercentage(completionPercentage)
                .totalHoursToLearn(totalHours)
                .generatedAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RoadmapHistoryResponse> getUserHistory(String userEmail) {
        return userRoadmapRepository.findByUserEmailOrderByCreatedAtDesc(userEmail).stream()
                .map(RoadmapHistoryResponse::from)
                .toList();
    }

    private List<RoadmapResponse.SkillDTO> toSkillDtos(List<Skill> skills, boolean alreadyKnown) {
        return skills.stream()
                .map(skill -> RoadmapResponse.SkillDTO.builder()
                        .id(skill.getId())
                        .name(skill.getName())
                        .category(skill.getCategory())
                        .priority(skill.getPriority())
                        .priorityLabel(skill.priorityLabel())
                        .description(skill.getDescription())
                        .estimatedHours(skill.getEstimatedHours())
                        .resourceUrl(skill.getResourceUrl())
                        .alreadyKnown(alreadyKnown)
                        .build())
                .toList();
    }
}
