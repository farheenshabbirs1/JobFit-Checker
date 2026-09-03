package com.farheenshaikh.jobfit.common.model;

/**
 * A resume's position in the multi-stage async pipeline. Each service owns the transitions
 * out of the stage it's responsible for: api-service sets {@link #UPLOADED}, parsing-service
 * moves {@link #UPLOADED} -> {@link #PARSING} -> {@link #PARSED} (or {@link #FAILED}),
 * analysis-service moves {@link #PARSED} -> {@link #ANALYZING} -> {@link #DONE} (or
 * {@link #FAILED}). The frontend polls a single resume resource to watch it move through
 * these states instead of needing to know about the outbox underneath.
 */
public enum ResumeStatus {
    UPLOADED,
    PARSING,
    PARSED,
    ANALYZING,
    DONE,
    FAILED
}
