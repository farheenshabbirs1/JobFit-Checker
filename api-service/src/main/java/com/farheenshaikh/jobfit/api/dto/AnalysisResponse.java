package com.farheenshaikh.jobfit.api.dto;

import com.farheenshaikh.jobfit.common.model.AnalysisResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** Response body for {@code GET /api/resumes/{id}/analysis} -- analysis-service's verdict. */
public class AnalysisResponse {

    private final Long resumeId;
    private final Long jobId;
    private final BigDecimal qualificationScore;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String suggestions;
    private final Instant createdAt;

    public AnalysisResponse(Long resumeId, Long jobId, BigDecimal qualificationScore, List<String> matchedSkills,
                             List<String> missingSkills, String suggestions, Instant createdAt) {
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.qualificationScore = qualificationScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.createdAt = createdAt;
    }

    public static AnalysisResponse from(AnalysisResult result) {
        return new AnalysisResponse(result.getResumeId(), result.getJobId(), result.getQualificationScore(),
                result.getMatchedSkills(), result.getMissingSkills(), result.getSuggestions(),
                result.getCreatedAt());
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public BigDecimal getQualificationScore() {
        return qualificationScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
