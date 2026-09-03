package com.farheenshaikh.jobfit.common.event;

import com.farheenshaikh.jobfit.common.model.OutboxEvent;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base class for a service's event consumer. On a fixed schedule it drains every currently
 * PENDING event on one topic, one at a time, claiming each with
 * {@code SELECT ... FOR UPDATE SKIP LOCKED} -- the mechanism that lets several instances of
 * the same service run concurrently and safely split the backlog instead of duplicating work.
 * This is what "independent horizontal scaling" means here: start a second instance of
 * parsing-service, and it starts claiming rows the first instance hasn't gotten to yet, no
 * coordination between them required beyond the database.
 *
 * <p>Each claim+handle+complete cycle is its own short transaction (via
 * {@link TransactionTemplate}, not {@code @Transactional} self-invocation, which would
 * silently no-op here since {@link #pollLoop()} would be calling a sibling method on `this`
 * rather than going through the Spring-managed proxy). A handler that throws marks the event
 * FAILED rather than losing it or retrying forever.
 */
public abstract class OutboxPoller {

    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private final String consumerId = resolveConsumerId();
    private TransactionTemplate transactionTemplate;

    @PostConstruct
    void init() {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /** The outbox topic this poller consumes, e.g. {@code "resume.uploaded"}. */
    protected abstract String topic();

    /** Handle one event's JSON payload. Throwing marks the event FAILED. */
    protected abstract void handle(String payloadJson) throws Exception;

    @Scheduled(fixedDelayString = "${jobfit.outbox.poll-interval-ms:500}")
    public void pollLoop() {
        while (Boolean.TRUE.equals(transactionTemplate.execute(status -> processNext()))) {
            // keep draining the backlog for this topic on every tick
        }
    }

    private boolean processNext() {
        Optional<OutboxEvent> claimed = claim();
        if (claimed.isEmpty()) {
            return false;
        }
        OutboxEvent event = claimed.get();
        try {
            handle(event.getPayload());
            markProcessed(event.getId());
        } catch (Exception e) {
            log.error("[{}] event {} on topic '{}' failed: {}", consumerId, event.getId(), topic(), e.toString(), e);
            markFailed(event.getId());
        }
        return true;
    }

    private Optional<OutboxEvent> claim() {
        @SuppressWarnings("unchecked")
        List<OutboxEvent> rows = entityManager.createNativeQuery(
                        "UPDATE event_outbox SET status = 'PROCESSING', processed_by = :consumer, "
                                + "attempts = attempts + 1 "
                                + "WHERE id = ("
                                + "  SELECT id FROM event_outbox WHERE topic = :topic AND status = 'PENDING' "
                                + "  ORDER BY id FOR UPDATE SKIP LOCKED LIMIT 1"
                                + ") RETURNING *",
                        OutboxEvent.class)
                .setParameter("consumer", consumerId)
                .setParameter("topic", topic())
                .getResultList();
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    private void markProcessed(Long id) {
        entityManager.createNativeQuery(
                        "UPDATE event_outbox SET status = 'PROCESSED', processed_at = now() WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    private void markFailed(Long id) {
        entityManager.createNativeQuery(
                        "UPDATE event_outbox SET status = 'FAILED' WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    private static String resolveConsumerId() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname != null && !hostname.isBlank()) {
            return hostname; // the pod/container name in a real deployment
        }
        return "instance-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
