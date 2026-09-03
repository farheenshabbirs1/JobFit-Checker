package com.farheenshaikh.jobfit.analysis.scoring;

import java.math.BigDecimal;
import java.util.List;

/** Output of a {@link QualificationScorer}: the number and which required skills matched or didn't. */
public record ScoreResult(BigDecimal qualificationScore, List<String> matchedSkills, List<String> missingSkills) {
}
