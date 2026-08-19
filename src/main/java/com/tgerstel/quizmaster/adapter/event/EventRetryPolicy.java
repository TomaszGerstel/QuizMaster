package com.tgerstel.quizmaster.adapter.event;

import com.tgerstel.quizmaster.adapter.persistence.EventDocument;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EventRetryPolicy {

    private static final int MAX_ATTEMPTS = 5;

    public boolean canRetry(EventDocument event) {
        return event.getAttempts() < MAX_ATTEMPTS;
    }

    public Instant nextAttemptAt(int attempts) {
        long delaySeconds = switch (attempts) {
            case 1 -> 30;
            case 2 -> 60;
            case 3 -> 300;
            case 4 -> 900;
            default -> 0;
        };

        return Instant.now().plusSeconds(delaySeconds);
    }
}
