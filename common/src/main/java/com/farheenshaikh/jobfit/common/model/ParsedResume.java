package com.farheenshaikh.jobfit.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

/**
 * The structured output of parsing-service's two-stage pipeline (raw text extraction, then
 * field extraction) for one resume. {@code resumeId} is both the primary key and the foreign
 * key -- there's exactly one of these per resume, written once.
 */
@Entity
@Table(name = "parsed_resumes")
public class ParsedResume {

    @Id
    @Column(name = "resume_id")
    private Long resumeId;

    @Column(name = "extracted_text", nullable = false, columnDefinition = "text")
    private String extractedText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> skills;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(length = 500)
    private String education;

    @Column(name = "parsed_at", nullable = false)
    private Instant parsedAt;

    protected ParsedResume() {
        // JPA
    }

    public ParsedResume(Long resumeId, String extractedText, List<String> skills,
                         Integer yearsExperience, String education) {
        this.resumeId = resumeId;
        this.extractedText = extractedText;
        this.skills = skills;
        this.yearsExperience = yearsExperience;
        this.education = education;
        this.parsedAt = Instant.now();
    }

    public Long getResumeId() {
        return resumeId;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public List<String> getSkills() {
        return skills;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public String getEducation() {
        return education;
    }

    public Instant getParsedAt() {
        return parsedAt;
    }
}
