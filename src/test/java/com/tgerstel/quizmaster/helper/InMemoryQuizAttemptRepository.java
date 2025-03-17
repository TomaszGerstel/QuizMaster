package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.adapter.persistence.QuizAttemptDocument;
import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Profile("test")
@Primary
public class InMemoryQuizAttemptRepository implements QuizAttemptRepository {

    private final List<QuizAttemptDocument> attemptDocuments = new ArrayList<>();

    @Override
    public void create(QuizAttemptDTO attempt) {
        attemptDocuments.add(QuizAttemptDocument.fromDTO(attempt));
    }

    @Override
    public Optional<QuizAttemptDTO> getAndEnd(String sessionId, Instant endTime) {
        Optional<QuizAttemptDocument> attempt = attemptDocuments.stream()
                .filter(a -> Objects.equals(a.getSessionId(), sessionId))
                .findFirst();
        attempt.ifPresent(a -> a.setEndTime(endTime));
        return attempt.map(QuizAttemptDocument::toDTO);
    }

    public void clear() {
        attemptDocuments.clear();
    }
}
