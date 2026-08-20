package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.adapter.persistence.EventDocument;
import com.tgerstel.quizmaster.adapter.persistence.EventRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Repository
@Profile("test")
@Primary
public class InMemoryEventRepository implements EventRepository {

    private final List<EventDocument> events = new ArrayList<>();

    @Override
    public void save(EventDocument eventDocument) {
        events.removeIf(event -> event.getId().equals(eventDocument.getId()));
        events.add(eventDocument);
    }

    @Override
    public Optional<EventDocument> claimNextEvent() {
        var found = events.stream()
                .sorted(Comparator.comparing(EventDocument::getCreatedAt))
                .filter(event -> event.getStatus().name().equals("PENDING")
                        && event.getNextAttemptAt().isBefore(Instant.now()))
                .findFirst();

        found.ifPresent(event -> {
            event.setStatus(com.tgerstel.quizmaster.domain.event.EventStatus.PROCESSING);
            event.setAttempts(event.getAttempts() + 1);
        });

        return found;
    }

    public void clear() {
        events.clear();
    }
}
