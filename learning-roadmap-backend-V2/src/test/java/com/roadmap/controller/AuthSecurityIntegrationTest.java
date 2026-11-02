package com.roadmap.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.roadmap.model.Role;
import com.roadmap.model.Skill;
import com.roadmap.repository.RoleRepository;
import com.roadmap.repository.SkillRepository;
import com.roadmap.repository.UserRepository;
import com.roadmap.repository.UserRoadmapRepository;

/** End-to-end tests through the real security filter chain, using an in-memory H2 database. */
@SpringBootTest
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    private static final String PASSWORD = "Password123";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired SkillRepository skillRepository;
    @Autowired UserRoadmapRepository userRoadmapRepository;

    private Long roleId;

    @BeforeEach
    void setUp() {
        userRoadmapRepository.deleteAll();
        skillRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.deleteAll();

        Role role = new Role();
        role.setName("Backend Developer");
        role.setCategory("Web Development");
        role.setDescription("Builds APIs");
        role = roleRepository.save(role);
        roleId = role.getId();

        skillRepository.save(skill("Java", 1, 80, role));
        skillRepository.save(skill("Spring Boot", 1, 60, role));
    }

    private static Skill skill(String name, int priority, int hours, Role role) {
        Skill s = new Skill();
        s.setName(name);
        s.setCategory("Test");
        s.setPriority(priority);
        s.setEstimatedHours(hours);
        s.setRole(role);
        return s;
    }

    private String json(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    private String register(String name, String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", name, "email", email, "password", PASSWORD))))
                .andExpect(status().isCreated())
                .andReturn();
        return JsonPath.<String>read(result.getResponse().getContentAsString(), "$.token");
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void registerReturnsTokenAndProfile() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Asha", "email", "Asha@Example.com", "password", PASSWORD))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }

    @Test
    void registerWithExistingEmailReturns409() throws Exception {
        register("Asha", "asha@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Other", "email", "asha@example.com", "password", PASSWORD))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "", "email", "not-an-email", "password", "123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    @Test
    void loginWithCorrectPasswordReturnsToken() throws Exception {
        register("Asha", "asha@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "asha@example.com", "password", PASSWORD))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordOrUnknownEmailReturns401() throws Exception {
        register("Asha", "asha@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "asha@example.com", "password", "WrongPassword1"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", "nobody@example.com", "password", PASSWORD))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithoutTokenReturns401Json() throws Exception {
        mockMvc.perform(get("/api/roadmap/history"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpointWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/roadmap/history").header(HttpHeaders.AUTHORIZATION, bearer("not.a.jwt")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void catalogIsPublic() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Backend Developer"));

        mockMvc.perform(get("/api/skills/role/" + roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void currentUserEndpointReturnsOwnProfile() throws Exception {
        String token = register("Asha", "asha@example.com");

        mockMvc.perform(get("/api/users/me").header(HttpHeaders.AUTHORIZATION, bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Asha"))
                .andExpect(jsonPath("$.email").value("asha@example.com"));
    }

    @Test
    void generateRoadmapUsesTokenIdentity() throws Exception {
        String token = register("Asha", "asha@example.com");

        mockMvc.perform(post("/api/roadmap/generate")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("targetRoleId", roleId, "currentSkills", List.of("java")))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("Asha"))
                .andExpect(jsonPath("$.userEmail").value("asha@example.com"))
                .andExpect(jsonPath("$.completionPercentage").value(50))
                .andExpect(jsonPath("$.totalHoursToLearn").value(60));
    }

    @Test
    void generateRoadmapForUnknownRoleReturns404() throws Exception {
        String token = register("Asha", "asha@example.com");

        mockMvc.perform(post("/api/roadmap/generate")
                        .header(HttpHeaders.AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("targetRoleId", 999999, "currentSkills", List.of("java")))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void historyOnlyShowsTheCallersOwnRoadmaps() throws Exception {
        String ashaToken = register("Asha", "asha@example.com");
        String raviToken = register("Ravi", "ravi@example.com");

        mockMvc.perform(post("/api/roadmap/generate")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ashaToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("targetRoleId", roleId, "currentSkills", List.of("java")))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/roadmap/history").header(HttpHeaders.AUTHORIZATION, bearer(ashaToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].skillsToLearn[0]").value("Spring Boot"));

        mockMvc.perform(get("/api/roadmap/history").header(HttpHeaders.AUTHORIZATION, bearer(raviToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
