package com.farheenshaikh.jobfit.parsing.skill;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Combines skill matching with two small regex heuristics -- years of experience and highest
 * education mentioned -- into the full field set {@code ParsedResume} stores. Heuristic and
 * intentionally conservative: it's fine for these to come back {@code null} on an
 * unconventionally formatted resume, since analysis-service treats missing fields as "unknown"
 * rather than "zero".
 */
@Component
public class ResumeFieldExtractor {

    private static final Pattern YEARS_PATTERN = Pattern.compile(
            "(\\d{1,2})\\+?\\s*(?:years?|yrs?)\\s*(?:of\\s*)?(?:professional\\s*)?experience",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern[] EDUCATION_PATTERNS = {
            Pattern.compile("(?i)\\b(ph\\.?d\\.?|doctorate)\\b.{0,80}"),
            Pattern.compile("(?i)\\b(m\\.?s\\.?|master'?s?|m\\.?eng\\.?)\\b.{0,80}"),
            Pattern.compile("(?i)\\b(b\\.?s\\.?|b\\.?a\\.?|bachelor'?s?)\\b.{0,80}"),
            Pattern.compile("(?i)\\b(associate'?s?)\\b.{0,80}"),
    };

    private final SkillExtractor skillExtractor;

    public ResumeFieldExtractor(SkillExtractor skillExtractor) {
        this.skillExtractor = skillExtractor;
    }

    public ExtractedFields extract(String text) {
        if (text == null) {
            return new ExtractedFields(java.util.List.of(), null, null);
        }
        return new ExtractedFields(skillExtractor.extractSkills(text), extractYearsExperience(text),
                extractEducation(text));
    }

    private Integer extractYearsExperience(String text) {
        Matcher matcher = YEARS_PATTERN.matcher(text);
        int max = -1;
        while (matcher.find()) {
            max = Math.max(max, Integer.parseInt(matcher.group(1)));
        }
        return max >= 0 ? max : null;
    }

    private String extractEducation(String text) {
        // Highest-degree-first: return the first (most senior) match, trimmed to one line.
        for (Pattern pattern : EDUCATION_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String snippet = matcher.group().replaceAll("\\s+", " ").trim();
                return snippet.length() > 200 ? snippet.substring(0, 200) : snippet;
            }
        }
        return null;
    }
}
