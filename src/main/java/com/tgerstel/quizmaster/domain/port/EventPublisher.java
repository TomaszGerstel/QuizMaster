package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.event.DomainEvent;

public interface EventPublisher {

    void publish(DomainEvent<?> event);
}