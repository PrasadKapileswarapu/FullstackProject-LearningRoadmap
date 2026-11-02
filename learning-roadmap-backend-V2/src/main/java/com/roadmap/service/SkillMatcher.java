package com.roadmap.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Decides whether a user already knows a required skill.
 * Matching ignores case and extra spaces. A combined skill such as "HTML &amp; CSS" counts as
 * known if the user lists the full name or every part ("HTML" and "CSS").
 */
public final class SkillMatcher {

    private SkillMatcher() {
    }

    public static String normalise(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    public static Set<String> normaliseAll(Collection<String> skills) {
        Set<String> result = new HashSet<>();
        if (skills != null) {
            for (String skill : skills) {
                String normalised = normalise(skill);
                if (!normalised.isEmpty()) {
                    result.add(normalised);
                }
            }
        }
        return result;
    }

    public static boolean isKnown(String requiredSkill, Set<String> knownSkills) {
        String required = normalise(requiredSkill);
        if (knownSkills.contains(required)) {
            return true;
        }
        if (!required.contains("&")) {
            return false;
        }
        return Arrays.stream(required.split("\\s*&\\s*"))
                .allMatch(part -> !part.isEmpty() && knownSkills.contains(part));
    }
}
