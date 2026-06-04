package com.roadmap.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.roadmap.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findByCategory(String category);

    boolean existsByNameIgnoreCase(String name);
}
