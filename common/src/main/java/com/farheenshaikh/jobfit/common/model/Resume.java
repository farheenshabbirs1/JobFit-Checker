package com.farheenshaikh.jobfit.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A single resume upload, tied to the job it was submitted against. {@code storageKey} is a
 * {@link com.farheenshaikh.jobfit.common.storage.BlobStorage} key, not a filesystem path or
 * an S3 URL -- which storage backend it resolves against is a deployment detail.
 */
@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResumeStatus status = ResumeStatus.UPLOADED;

    @Column(name = "failure_reason", columnDefinition = "text")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Resume() {
        // JPA
    }

    public Resume(Long jobId, String candidateName, String originalFilename, String contentType,
                  String storageKey) {
        this.jobId = jobId;
        this.candidateName = candidateName;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markStatus(ResumeStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = ResumeStatus.FAILED;
        this.failureReason = reason;
        this.updatedAt = Instant.now();
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

    public String getContentType() {
        return contentType;
    }

    public String getStorageKey() {
        return storageKey;
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
