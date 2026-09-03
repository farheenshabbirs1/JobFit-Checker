package com.farheenshaikh.jobfit.analysis.scoring;

import java.util.List;

/** Scores how well a candidate's extracted skills cover a job's required skills. */
public interface QualificationScorer {

    ScoreResult score(List<String> requiredSkills, List<String> candidateSkills);
}
