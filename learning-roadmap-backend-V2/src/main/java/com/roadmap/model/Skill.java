package com.roadmap.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    /** 1 = must know, 2 = should know, 3 = good to know */
    @Column(nullable = false)
    private Integer priority;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer estimatedHours;

    private String resourceUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /** Human readable label for the numeric priority. */
    public String priorityLabel() {
        return switch (priority == null ? 0 : priority) {
            case 1 -> "Must Know";
            case 2 -> "Should Know";
            case 3 -> "Good to Know";
            default -> "Optional";
        };
    }
}
