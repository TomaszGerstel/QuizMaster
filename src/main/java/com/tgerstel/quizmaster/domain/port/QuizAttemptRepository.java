package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.AttemptToEvalDTO;
import com.tgerstel.quizmaster.domain.command.EndAttemptCommand;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;

import java.util.Optional;

public interface QuizAttemptRepository {
    Optional<QuizAttempt> findBySessionId(String sessionId);
    void create(QuizAttempt attempt);
    Optional<AttemptToEvalDTO> getToEval(String sessionId);
    void endAttempt(EndAttemptCommand command);
}
