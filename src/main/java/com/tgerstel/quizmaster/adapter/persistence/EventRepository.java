package com.tgerstel.quizmaster.adapter.persistence;

import java.util.Optional;

public interface EventRepository {

    void save(EventDocument eventDocument);
    Optional<EventDocument> claimNextEvent();
}
