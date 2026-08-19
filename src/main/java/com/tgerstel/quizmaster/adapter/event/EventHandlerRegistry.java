package com.tgerstel.quizmaster.adapter.event;

import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventHandlerRegistry {

    private final Map<EventType, EventHandler<?>> handlers;

    public EventHandlerRegistry(List<EventHandler<?>> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toMap(
                        EventHandler::eventType,
                        Function.identity()
                ));
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainEvent.Payload>
    EventHandler<T> getHandler(EventType eventType) {

        var handler = handlers.get(eventType);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "No handler found for event type: " + eventType
            );
        }

        return (EventHandler<T>) handler;
    }
}
