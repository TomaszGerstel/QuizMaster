package com.tgerstel.quizmaster.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
@AllArgsConstructor
public class QuizAttemptDTO {
    private String sessionId;
    private String quizId;
    private Instant startTime;
    private Instant endTime;

    public static QuizAttemptDTO startAttempt(String sessionId, String quizId) {
        Instant start = Instant.now();
        return new QuizAttemptDTO(sessionId, quizId, start, null);
    }

    public Duration getQuizTime() {
        if (endTime == null || startTime == null) return null;
        return Duration.between(startTime, endTime);
    }

}
