package com.tgerstel.quizmaster.adapter.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgerstel.quizmaster.adapter.persistence.EventDocument;
import com.tgerstel.quizmaster.adapter.persistence.EventRepository;
import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final EventRepository eventRepository;
    private final EventHandlerRegistry handlerRegistry;
    private final ObjectMapper objectMapper;
    private final EventRetryPolicy retryPolicy;

    public void process() {

        var eventOptional = eventRepository.claimNextEvent();

        if (eventOptional.isEmpty()) {
            return;
        }

        var event = eventOptional.get();

        process(event);
    }

    private void process(EventDocument event) {

        try {
            var handler = handlerRegistry.getHandler(event.getType());

            var payload = objectMapper.readValue(event.getPayload(), event.getType().getPayloadClass());

            var domainEvent = new DomainEvent<>(
                    event.getId(),
                    event.getType(),
                    event.getCreatedAt(),
                    payload
            );

            handler.handle(domainEvent);

            event.setStatus(EventStatus.COMPLETED);
            event.setProcessedAt(Instant.now());
            event.setLastError(null);

            eventRepository.save(event);

            log.info("Event {} processed successfully after {} attempt(s)", event.getId(), event.getAttempts());

        } catch (Exception e) {
            handleFailure(event, e);
        }
    }

    private void handleFailure(EventDocument event, Exception exception) {

        log.error("Failed to process event {}. Attempt {}", event.getId(), event.getAttempts(), exception);

        if (retryPolicy.canRetry(event)) {
            event.setStatus(EventStatus.PENDING);
            event.setNextAttemptAt(retryPolicy.nextAttemptAt(event.getAttempts()));
            event.setLastError(exception.getMessage());

            eventRepository.save(event);

            log.info("Event {} scheduled for retry at {}", event.getId(), event.getNextAttemptAt());

        } else {

            event.setStatus(EventStatus.FAILED);
            event.setLastError(exception.getMessage());

            eventRepository.save(event);

            log.error("Event {} permanently failed after {} attempts", event.getId(), event.getAttempts());
        }
    }
}
