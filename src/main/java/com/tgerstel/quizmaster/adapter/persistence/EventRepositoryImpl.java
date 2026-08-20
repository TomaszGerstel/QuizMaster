package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.event.EventStatus;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@Profile({"prod", "dev", "dokploy"})
public class EventRepositoryImpl implements EventRepository {

    private final EventMongoRepository eventMongoRepository;

    public EventRepositoryImpl(EventMongoRepository eventMongoRepository) {
        this.eventMongoRepository = eventMongoRepository;
    }

    @Override
    public void save(EventDocument eventDocument) {
        eventMongoRepository.save(eventDocument);
    }

    @Override
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
