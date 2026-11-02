package com.roadmap.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadmap.dto.RoleResponse;
import com.roadmap.dto.SkillResponse;
import com.roadmap.exception.ResourceNotFoundException;
import com.roadmap.repository.RoleRepository;
import com.roadmap.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

/** Read-only access to the roles and skills catalogue. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(RoleResponse::from).toList();
    }

    public RoleResponse getRole(Long id) {
        return roleRepository.findById(id)
                .map(RoleResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));
    }

    public List<RoleResponse> getRolesByCategory(String category) {
        return roleRepository.findByCategory(category).stream().map(RoleResponse::from).toList();
    }

    public List<SkillResponse> getSkillsByRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with ID: " + roleId);
        }
        return skillRepository.findByRoleIdOrderByPriorityAsc(roleId).stream()
                .map(SkillResponse::from)
                .toList();
    }
}
