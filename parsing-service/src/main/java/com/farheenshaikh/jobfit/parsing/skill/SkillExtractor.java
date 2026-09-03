package com.farheenshaikh.jobfit.parsing.skill;

import java.util.List;

/** Stage 2 of parsing: pull structured skill keywords out of extracted resume text. */
public interface SkillExtractor {

    List<String> extractSkills(String text);
}
