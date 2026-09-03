package com.farheenshaikh.jobfit.common.event.payload;

/**
 * Payload for the {@code "resume.parsed"} topic. Published by parsing-service once it has
 * written a {@code ParsedResume} row; consumed by analysis-service, which loads that row
 * (plus the {@code Job} it's being scored against) from the database itself rather than
 * having the extracted text pushed through the event -- keeps the event small and lets the
 * two services evolve their schemas independently.
 */
public class ResumeParsedEvent {

    private Long resumeId;
    private Long jobId;

    protected ResumeParsedEvent() {
        // Jackson
    }

    public ResumeParsedEvent(Long resumeId, Long jobId) {
        this.resumeId = resumeId;
        this.jobId = jobId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public Long getJobId() {
        return jobId;
    }
}
