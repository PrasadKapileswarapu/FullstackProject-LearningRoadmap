package com.roadmap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roadmap.model.Role;
import com.roadmap.model.Skill;
import com.roadmap.repository.RoleRepository;
import com.roadmap.repository.SkillRepository;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SkillController {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;


    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {

        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }


    @GetMapping("/roles/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/roles/category/{category}")
    public ResponseEntity<List<Role>> getRolesByCategory(@PathVariable String category) {
        List<Role> roles = roleRepository.findByCategory(category);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/skills/role/{roleId}")
    public ResponseEntity<List<Skill>> getSkillsByRole(@PathVariable Long roleId) {
        List<Skill> skills = skillRepository.findByRoleIdOrderByPriorityAsc(roleId);
        return ResponseEntity.ok(skills);
    }
}
