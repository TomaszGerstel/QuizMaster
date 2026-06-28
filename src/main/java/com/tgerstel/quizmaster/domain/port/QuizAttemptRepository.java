package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.AttemptToEvalDTO;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;

import java.time.Instant;
import java.util.Optional;

public interface QuizAttemptRepository {
    void create(QuizAttempt attempt);
    Optional<AttemptToEvalDTO> getToEval(String sessionId);
    void endAttempt(String sessionId, Instant endTime, int score);
}
