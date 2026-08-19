package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.event.EventStatus;
import com.tgerstel.quizmaster.domain.event.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Data
@Document(collection = "domain_events")
@NoArgsConstructor
public class EventDocument {

    @Id
    private UUID id;
    private EventType type;
    private String payload;
    private EventStatus status;
    private int attempts;

    private Instant createdAt;
    private Instant nextAttemptAt;
    private Instant processedAt;

    private String lastError;

}
