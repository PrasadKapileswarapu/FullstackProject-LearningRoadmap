package com.roadmap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roadmap.dto.RoleResponse;
import com.roadmap.dto.SkillResponse;
import com.roadmap.service.CatalogService;

import lombok.RequiredArgsConstructor;

/** Public, read-only catalogue of roles and skills (no JWT needed). */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SkillController {

    private final CatalogService catalogService;

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(catalogService.getAllRoles());
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getRole(id));
    }

    @GetMapping("/roles/category/{category}")
    public ResponseEntity<List<RoleResponse>> getRolesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(catalogService.getRolesByCategory(category));
    }

    @GetMapping("/skills/role/{roleId}")
    public ResponseEntity<List<SkillResponse>> getSkillsByRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(catalogService.getSkillsByRole(roleId));
    }
}
