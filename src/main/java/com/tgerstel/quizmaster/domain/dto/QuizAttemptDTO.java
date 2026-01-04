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
    private String userName;
    private String userEmail;
    private Instant startTime;
    private Instant endTime;
    private int questionsCount;
    private int correctAnswers;

    public static QuizAttemptDTO startAttempt(String sessionId, String quizId, int questions, String userName,
                                              String userEmail) {
        Instant start = Instant.now();
        return new QuizAttemptDTO(sessionId, quizId, userName, userEmail, start, null, questions, 0);
    }

    public Duration getQuizTime() {
        if (endTime == null || startTime == null) return null;
        return Duration.between(startTime, endTime);
    }

}
