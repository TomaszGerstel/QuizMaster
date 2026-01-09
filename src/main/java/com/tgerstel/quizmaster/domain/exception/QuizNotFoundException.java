package com.tgerstel.quizmaster.domain.exception;

public class QuizNotFoundException extends RuntimeException {

    public QuizNotFoundException(String quizId) {
        super("Quiz with ID " + quizId + " not found");
    }
}
