package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.event.EventStatus;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventRepository {

    private final EventMongoRepository eventMongoRepository;

    public EventRepository(EventMongoRepository eventMongoRepository) {
        this.eventMongoRepository = eventMongoRepository;
    }

    public void save(EventDocument eventDocument) {
        eventMongoRepository.save(eventDocument);
    }

    public Optional<EventDocument> claimNextEvent() {

        return eventMongoRepository
                .findFirstByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(EventStatus.PENDING, Instant.now())
                .map(event -> {
                    event.setStatus(EventStatus.PROCESSING);
                    event.setAttempts(event.getAttempts() + 1);
                    eventMongoRepository.save(event);
                    return event;
                });
    }

}
