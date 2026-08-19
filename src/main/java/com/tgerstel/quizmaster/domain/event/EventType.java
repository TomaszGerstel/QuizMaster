package com.tgerstel.quizmaster.domain.event;

import lombok.Getter;

@Getter
public enum EventType {

    QUIZ_COMPLETED(QuizCompletedEventPayload.class);

    private final Class<? extends DomainEvent.Payload> payloadClass;

    EventType(Class<? extends DomainEvent.Payload> payloadClass) {
        this.payloadClass = payloadClass;
    }


}
