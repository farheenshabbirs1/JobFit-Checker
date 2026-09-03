package com.farheenshaikh.jobfit.analysis.suggestion;

import com.farheenshaikh.jobfit.analysis.scoring.ScoreResult;
import com.farheenshaikh.jobfit.common.model.Job;
import com.farheenshaikh.jobfit.common.model.ParsedResume;

/** Turns a {@link ScoreResult} into candidate-facing, actionable text. */
public interface SuggestionGenerator {

    String generate(Job job, ParsedResume parsedResume, ScoreResult scoreResult);
}
