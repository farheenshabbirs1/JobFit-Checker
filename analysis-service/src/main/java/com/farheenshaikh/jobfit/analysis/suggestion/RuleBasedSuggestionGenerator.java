package com.farheenshaikh.jobfit.analysis.suggestion;

import com.farheenshaikh.jobfit.analysis.scoring.ScoreResult;
import com.farheenshaikh.jobfit.common.model.Job;
import com.farheenshaikh.jobfit.common.model.ParsedResume;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Default {@link SuggestionGenerator}: a handful of straightforward rules over the skill gap
 * and the parsed years-of-experience field. No external AI call, so the pipeline runs with
 * zero services beyond Postgres -- an LLM-backed generator is a drop-in swap behind the same
 * interface if richer, more personalized suggestions are wanted later.
 */
@Component
public class RuleBasedSuggestionGenerator implements SuggestionGenerator {

    @Override
    public String generate(Job job, ParsedResume parsedResume, ScoreResult scoreResult) {
        StringBuilder sb = new StringBuilder();

        List<String> missing = scoreResult.missingSkills();
        if (missing.isEmpty()) {
            sb.append("This resume covers every required skill listed for ")
                    .append(job.getTitle())
                    .append(". ");
        } else {
            sb.append("To better match ")
                    .append(job.getTitle())
                    .append(", consider adding or highlighting experience with: ")
                    .append(String.join(", ", missing))
                    .append(". ");
            if (missing.size() == 1) {
                sb.append("Even a small project or certification in ")
                        .append(missing.get(0))
                        .append(" would close this gap. ");
            }
        }

        Integer years = parsedResume.getYearsExperience();
        if (years == null) {
            sb.append("Couldn't detect a total years-of-experience figure on this resume -- ")
                    .append("stating it explicitly (e.g. \"5+ years\") near the top may help automated screens. ");
        } else if (years < 2) {
            sb.append("With ").append(years).append(" year(s) of experience noted, ")
                    .append("emphasizing concrete project outcomes can help offset a shorter track record. ");
        }

        double matchRatio = job.getRequiredSkills().isEmpty()
                ? 1.0
                : (double) scoreResult.matchedSkills().size() / job.getRequiredSkills().size();
        if (matchRatio >= 0.8) {
            sb.append("Overall this is a strong match for the role.");
        } else if (matchRatio >= 0.5) {
            sb.append("Overall this is a moderate match -- the gaps above are worth addressing.");
        } else {
            sb.append("Overall this resume covers less than half of the listed required skills.");
        }

        return sb.toString();
    }
}
