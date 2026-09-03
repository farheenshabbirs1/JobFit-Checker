package com.farheenshaikh.jobfit.common.event;

import com.farheenshaikh.jobfit.common.model.OutboxEvent;
import com.farheenshaikh.jobfit.common.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * The only {@link EventPublisher} implementation: inserts a row into {@code event_outbox}.
 * Because this is a plain repository save, it participates in whatever transaction the
 * caller is already in -- there is deliberately no {@code @Transactional} here, so the
 * insert commits (or rolls back) exactly with the business write around it.
 */
@Component
public class OutboxEventPublisher implements EventPublisher {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(OutboxEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String topic, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            repository.save(new OutboxEvent(topic, json));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("failed to serialize event payload for topic '" + topic + "'", e);
        }
    }
}
