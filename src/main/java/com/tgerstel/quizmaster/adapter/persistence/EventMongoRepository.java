package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.event.EventStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EventMongoRepository extends MongoRepository<EventDocument, UUID> {

    Optional<EventDocument> findFirstByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            EventStatus status,
            Instant now
    );
}
