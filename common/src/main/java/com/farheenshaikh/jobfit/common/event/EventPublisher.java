package com.farheenshaikh.jobfit.common.event;

/**
 * Publishes an event to the outbox. Call this inside the same {@code @Transactional} method
 * that makes the business write it describes -- e.g. saving a {@code Resume} row and
 * publishing "resume.uploaded" in the same transaction -- so the event can never be silently
 * lost between the write committing and the event being visible to consumers.
 */
public interface EventPublisher {

    /**
     * @param topic   a dot-separated event name, e.g. {@code "resume.uploaded"}
     * @param payload any object serializable by Jackson; consumers deserialize it themselves
     */
    void publish(String topic, Object payload);
}
