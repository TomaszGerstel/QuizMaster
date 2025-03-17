package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;

import java.time.Instant;
import java.util.Optional;

public interface QuizAttemptRepository {
    void create(QuizAttemptDTO attempt);
    Optional<QuizAttemptDTO> getAndEnd(String sessionId, Instant endTime);
}
