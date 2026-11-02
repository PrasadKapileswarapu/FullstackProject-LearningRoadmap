package com.roadmap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SkillMatcherTest {

    @Test
    void matchesIgnoringCaseAndWhitespace() {
        Set<String> known = SkillMatcher.normaliseAll(List.of("  JAVA ", "spring   boot"));

        assertTrue(SkillMatcher.isKnown("Java", known));
        assertTrue(SkillMatcher.isKnown("Spring Boot", known));
        assertFalse(SkillMatcher.isKnown("Docker", known));
    }

    @Test
    void combinedSkillNeedsEveryPart() {
        assertTrue(SkillMatcher.isKnown("HTML & CSS", SkillMatcher.normaliseAll(List.of("html", "css"))));
        assertTrue(SkillMatcher.isKnown("HTML & CSS", SkillMatcher.normaliseAll(List.of("HTML & CSS"))));
        assertFalse(SkillMatcher.isKnown("HTML & CSS", SkillMatcher.normaliseAll(List.of("html"))));
    }

    @Test
    void nullAndBlankInputsAreIgnored() {
        assertTrue(SkillMatcher.normaliseAll(null).isEmpty());
        assertEquals(Set.of("java"), SkillMatcher.normaliseAll(Arrays.asList("Java", null, "  ")));
    }
}
