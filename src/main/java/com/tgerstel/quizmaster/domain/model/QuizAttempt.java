package com.tgerstel.quizmaster.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class QuizAttempt {
    private String sessionId;
    private String quizId;
    private String quizName;
    private String quizDescription;
    private Long quizVersion;
    private Integer passRate;
    private String userName;
    private String userEmail;
    private Boolean sendEmail;
    private Instant startTime;
    private Instant endTime;
    private Long quizDurationSeconds;
    private int questionsCount;
    private int correctAnswers;
    private Boolean isPassed;

    private List<Question> questions;

    public static QuizAttempt startAttempt(
            String sessionId,
            String quizId,
            String quizName,
            String quizDescription,
            Long quizVersion,
            Integer passRate,
            List<Question> questions,
            String userName,
            String userEmail

    ) {
        Instant start = Instant.now();

        return new QuizAttempt(
                sessionId,
                quizId,
                quizName,
                quizDescription,
                quizVersion,
                passRate,
                userName,
                userEmail,
                true,
                start,
                null,
                null,
                questions.size(),
                0,
                null,
                questions
        );
    }

}
