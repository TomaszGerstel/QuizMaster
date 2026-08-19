package com.tgerstel.quizmaster.domain.event;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class DomainEvent<T extends DomainEvent.Payload> {

    private final UUID eventId;
    private EventType type;
    private final Instant occurredAt;
    private T payload;

    public DomainEvent(UUID eventId, EventType type, Instant occurredAt, T payload) {
        this.eventId = eventId;
        this.type = type;
        this.occurredAt = occurredAt;
        this.payload = payload;
    }

    public static class Payload {

    }
}
