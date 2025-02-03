package com.tgerstel.quizmaster.domain.model;

public record QuizResult (String quizId, boolean isPositive, Integer quizScore,
    Integer questionsCount) {
}
