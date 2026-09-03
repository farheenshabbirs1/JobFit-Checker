package com.farheenshaikh.jobfit.parsing.skill;

import java.util.List;

/** Everything {@code ResumeFieldExtractor} pulls out of resume text, ready to save as a ParsedResume. */
public record ExtractedFields(List<String> skills, Integer yearsExperience, String education) {
}
