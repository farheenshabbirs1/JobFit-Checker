package com.farheenshaikh.jobfit.analysis.scoring;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link QualificationScorer}: qualification score is simply the percentage of the
 * job's required skills the candidate's extracted skills cover, case- and
 * whitespace-insensitively. Deliberately simple and explainable -- the missing-skills list it
 * produces is exactly what {@code RuleBasedSuggestionGenerator} turns into suggestions, so a
 * candidate can see precisely why they got the score they did.
 */
@Component
public class OverlapScorer implements QualificationScorer {

    @Override
    public ScoreResult score(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return new ScoreResult(BigDecimal.ZERO, List.of(), List.of());
        }

        List<String> candidate = candidateSkills != null ? candidateSkills : List.of();

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String required : requiredSkills) {
            String trimmedRequired = required.trim();
            boolean has = candidate.stream().anyMatch(s -> s.trim().equalsIgnoreCase(trimmedRequired));
            (has ? matched : missing).add(required);
        }

        BigDecimal score = BigDecimal.valueOf(matched.size())
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(requiredSkills.size()), 2, RoundingMode.HALF_UP);

        return new ScoreResult(score, matched, missing);
    }
}
