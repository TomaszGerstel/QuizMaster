package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;

import java.time.Instant;
import java.util.List;

public record AttemptToEvalDTO(
        String sessionId,
        String quizId,
        String email,
        Instant startTime,
        List<EvalQuestion> questions
) {
}
