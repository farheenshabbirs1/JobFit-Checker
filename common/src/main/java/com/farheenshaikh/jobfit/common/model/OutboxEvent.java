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

/**
 * One row in the transactional outbox -- the event bus every service uses instead of a
 * cloud message queue. A producer inserts a row in the same database transaction as its
 * business write (so the event can never be "lost" between the write and the publish); a
 * consumer claims a row with {@code SELECT ... FOR UPDATE SKIP LOCKED} (see
 * {@code OutboxPoller}), which is what lets several instances of the same service safely
 * compete for work.
 */
@Entity
@Table(name = "event_outbox")
public class OutboxEvent {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_PROCESSED = "PROCESSED";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private String status = STATUS_PENDING;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "processed_by")
    private String processedBy;

    protected OutboxEvent() {
        // JPA
    }

    public OutboxEvent(String topic, String payloadJson) {
        this.topic = topic;
        this.payload = payloadJson;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public String getProcessedBy() {
        return processedBy;
    }
}
