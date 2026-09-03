package com.farheenshaikh.jobfit.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** analysis-service's verdict on how well one resume fits one job. One row per resume. */
@Entity
@Table(name = "analysis_results")
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "qualification_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal qualificationScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "matched_skills", nullable = false, columnDefinition = "jsonb")
    private List<String> matchedSkills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "missing_skills", nullable = false, columnDefinition = "jsonb")
    private List<String> missingSkills;

    @Column(nullable = false, columnDefinition = "text")
    private String suggestions;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AnalysisResult() {
        // JPA
    }

    public AnalysisResult(Long jobId, Long resumeId, BigDecimal qualificationScore,
                           List<String> matchedSkills, List<String> missingSkills, String suggestions) {
        this.jobId = jobId;
        this.resumeId = resumeId;
        this.qualificationScore = qualificationScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suggestions = suggestions;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public Long getResumeId() {
        return resumeId;
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
