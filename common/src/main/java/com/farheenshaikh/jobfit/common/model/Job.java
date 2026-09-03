package com.farheenshaikh.jobfit.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

/** A job posting: what analysis-service scores parsed resumes against. */
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    /** Skill keywords required for the role, e.g. ["java", "spring boot", "postgresql"]. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_skills", nullable = false, columnDefinition = "jsonb")
    private List<String> requiredSkills;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Job() {
        // JPA
    }

    public Job(String title, String description, List<String> requiredSkills) {
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
