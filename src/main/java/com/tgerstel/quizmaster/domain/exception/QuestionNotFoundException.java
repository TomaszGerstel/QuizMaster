package com.tgerstel.quizmaster.domain.exception;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(String id) {
        super("Question with ID " + id + " not found.");
    }
}
