package com.farheenshaikh.jobfit.common.event.payload;

/**
 * Payload for the {@code "resume.uploaded"} topic. Published by api-service the moment a
 * resume file and its {@code Resume} row exist; consumed by parsing-service, which reads the
 * file back out of {@link com.farheenshaikh.jobfit.common.storage.BlobStorage} via
 * {@code storageKey} and runs its two-stage extraction. Intentionally carries only IDs and
 * storage coordinates, not resume content -- the event is a pointer, not a payload of data.
 */
public class ResumeUploadedEvent {

    private Long resumeId;
    private Long jobId;
    private String storageKey;
    private String originalFilename;
    private String contentType;

    protected ResumeUploadedEvent() {
        // Jackson
    }

    public ResumeUploadedEvent(Long resumeId, Long jobId, String storageKey, String originalFilename,
                                String contentType) {
        this.resumeId = resumeId;
        this.jobId = jobId;
        this.storageKey = storageKey;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }
}
