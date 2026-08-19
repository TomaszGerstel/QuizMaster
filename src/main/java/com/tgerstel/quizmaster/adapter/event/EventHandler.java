package com.tgerstel.quizmaster.adapter.event;

import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventType;

public interface EventHandler <T extends DomainEvent.Payload> {

    EventType eventType();

    void handle(DomainEvent<T> event);
}