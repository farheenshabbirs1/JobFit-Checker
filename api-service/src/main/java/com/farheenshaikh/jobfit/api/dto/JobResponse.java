package com.farheenshaikh.jobfit.api.dto;

import com.farheenshaikh.jobfit.common.model.Job;

import java.time.Instant;
import java.util.List;

/** Response body for job-posting endpoints. */
public class JobResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final List<String> requiredSkills;
    private final Instant createdAt;

    public JobResponse(Long id, String title, String description, List<String> requiredSkills, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requiredSkills = requiredSkills;
        this.createdAt = createdAt;
    }

    public static JobResponse from(Job job) {
        return new JobResponse(job.getId(), job.getTitle(), job.getDescription(), job.getRequiredSkills(),
                job.getCreatedAt());
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
