package com.farheenshaikh.jobfit.api.dto;

import com.farheenshaikh.jobfit.common.model.Resume;
import com.farheenshaikh.jobfit.common.model.ResumeStatus;

import java.time.Instant;

/**
 * Response body for resume endpoints. This is the resource the frontend polls to watch a
 * resume move through {@link ResumeStatus} -- upload once, then GET this until
 * {@code status} is {@code DONE} or {@code FAILED}.
 */
public class ResumeResponse {

    private final Long id;
    private final Long jobId;
    private final String candidateName;
    private final String originalFilename;
    private final ResumeStatus status;
    private final String failureReason;
    private final Instant createdAt;
    private final Instant updatedAt;

    public ResumeResponse(Long id, Long jobId, String candidateName, String originalFilename,
                           ResumeStatus status, String failureReason, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.jobId = jobId;
        this.candidateName = candidateName;
        this.originalFilename = originalFilename;
        this.status = status;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ResumeResponse from(Resume resume) {
        return new ResumeResponse(resume.getId(), resume.getJobId(), resume.getCandidateName(),
                resume.getOriginalFilename(), resume.getStatus(), resume.getFailureReason(),
                resume.getCreatedAt(), resume.getUpdatedAt());
    }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public ResumeStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
