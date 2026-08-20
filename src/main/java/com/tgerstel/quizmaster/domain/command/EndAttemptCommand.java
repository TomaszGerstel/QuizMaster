package com.tgerstel.quizmaster.domain.command;

import java.time.Instant;

public record EndAttemptCommand(
    String attemptId,
    Instant endTime,
    long attemptDuration,
    int score,
    int passRate,
    Boolean isPassed
) {
}
