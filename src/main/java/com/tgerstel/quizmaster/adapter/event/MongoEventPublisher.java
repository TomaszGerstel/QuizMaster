package com.tgerstel.quizmaster.adapter.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgerstel.quizmaster.adapter.persistence.EventDocument;
import com.tgerstel.quizmaster.adapter.persistence.EventRepository;
import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventStatus;
import com.tgerstel.quizmaster.domain.exception.EventPublicationException;
import com.tgerstel.quizmaster.domain.port.EventPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MongoEventPublisher implements EventPublisher {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(DomainEvent<?> event) {
        EventDocument eventDocument;
        try {
            eventDocument = from(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event payload for event: {}", event, e);
            throw new EventPublicationException("Failed to serialize event: " + event.getType(), e);
        } catch ( DataAccessException e) {
            log.error("Database access error while publishing event: {}", event, e);
            throw new EventPublicationException("Database access error while publishing event: " + event.getType(), e);
        } catch (Exception e) {
            log.error("Unexpected error while publishing event: {}", event, e);
            throw new EventPublicationException("Unexpected error while publishing event: " + event.getType(), e);
        }
        eventRepository.save(eventDocument);
    }

    private EventDocument from(DomainEvent<?> event) throws JsonProcessingException {
        EventDocument document = new EventDocument();
        document.setId(event.getEventId());
        document.setType(event.getType());
        document.setPayload(objectMapper.writeValueAsString(event.getPayload()));
        document.setCreatedAt(event.getOccurredAt());
        document.setNextAttemptAt(event.getOccurredAt());
        document.setAttempts(0);
        document.setStatus(EventStatus.PENDING);
        return document;
    }
}