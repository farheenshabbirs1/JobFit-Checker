package com.farheenshaikh.jobfit.common.repository;

import com.farheenshaikh.jobfit.common.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Plain CRUD for {@code event_outbox} -- used by {@code OutboxEventPublisher} to insert new
 * events. Claiming and completing events uses raw SQL instead (see {@code OutboxPoller}),
 * because the claim needs {@code SELECT ... FOR UPDATE SKIP LOCKED}, which the Spring Data
 * query-derivation mechanism doesn't express.
 */
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
}
