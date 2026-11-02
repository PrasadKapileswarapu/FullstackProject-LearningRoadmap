package com.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class RoadmapServiceTest {

    private static final String EMAIL = "asha@example.com";

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock SkillRepository skillRepository;
    @Mock UserRoadmapRepository userRoadmapRepository;

    @InjectMocks RoadmapService service;

    private AppUser user;
    private Role role;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(7L);
        user.setFullName("Asha");
        user.setEmail(EMAIL);

        role = new Role();
        role.setId(1L);
        role.setName("Backend Developer");
        role.setCategory("Web Development");
    }

    private static Skill skill(long id, String name, int priority, int hours) {
        Skill s = new Skill();
        s.setId(id);
        s.setName(name);
        s.setCategory("Test");
        s.setPriority(priority);
        s.setEstimatedHours(hours);
        return s;
    }

    private void stubSave() {
        when(userRoadmapRepository.save(any(UserRoadmap.class))).thenAnswer(invocation -> {
            UserRoadmap r = invocation.getArgument(0);
            r.setId(99L);
            r.setCreatedAt(LocalDateTime.now());
            return r;
        });
    }

    @Test
    void calculatesCompletionHoursAndMissingSkills() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(skillRepository.findByRoleIdOrderByPriorityAsc(1L)).thenReturn(List.of(
                skill(1, "Java", 1, 80),
                skill(2, "Spring Boot", 1, 60),
                skill(3, "HTML & CSS", 1, 30),
                skill(4, "Docker", 3, 20)));
        stubSave();

        RoadmapResponse response = service.generateRoadmap(EMAIL,
                new RoadmapRequest(1L, List.of(" JAVA ", "html", "CSS")));

        assertEquals(99L, response.getRoadmapId());
        assertEquals("Asha", response.getUserName());
        assertEquals(4, response.getTotalSkillsRequired());
        assertEquals(2, response.getSkillsAlreadyKnown());
        assertEquals(2, response.getSkillsToLearnCount());
        assertEquals(50, response.getCompletionPercentage());
        assertEquals(80, response.getTotalHoursToLearn());
        assertEquals("Must Know", response.getSkillsToLearn().get(0).getPriorityLabel());

        ArgumentCaptor<UserRoadmap> captor = ArgumentCaptor.forClass(UserRoadmap.class);
        verify(userRoadmapRepository).save(captor.capture());
        assertEquals("Spring Boot, Docker", captor.getValue().getSkillsToLearn());
        assertEquals(EMAIL, captor.getValue().getUserEmail());
        assertEquals("Backend Developer", captor.getValue().getTargetRole());
    }

    @Test
    void handlesNullSkillsAndRoleWithNoSkills() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(skillRepository.findByRoleIdOrderByPriorityAsc(1L)).thenReturn(List.of());
        stubSave();

        RoadmapResponse response = service.generateRoadmap(EMAIL, new RoadmapRequest(1L, null));

        assertEquals(0, response.getCompletionPercentage());
        assertEquals(0, response.getTotalHoursToLearn());
        assertEquals(0, response.getTotalSkillsRequired());
    }

    @Test
    void unknownRoleThrowsNotFoundAndSavesNothing() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(roleRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.generateRoadmap(EMAIL, new RoadmapRequest(42L, List.of("java"))));

        verify(userRoadmapRepository, never()).save(any());
    }

    @Test
    void historyIsMappedToDtos() {
        UserRoadmap saved = new UserRoadmap();
        saved.setId(5L);
        saved.setTargetRole("Backend Developer");
        saved.setCurrentSkills("Java, Git");
        saved.setSkillsToLearn("");
        saved.setCompletionPercentage(40);
        when(userRoadmapRepository.findByUserEmailOrderByCreatedAtDesc(EMAIL)).thenReturn(List.of(saved));

        List<RoadmapHistoryResponse> history = service.getUserHistory(EMAIL);

        assertEquals(1, history.size());
        assertEquals(List.of("Java", "Git"), history.get(0).currentSkills());
        assertEquals(List.of(), history.get(0).skillsToLearn());
    }
}
