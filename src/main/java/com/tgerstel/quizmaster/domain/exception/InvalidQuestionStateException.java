package com.tgerstel.quizmaster.domain.exception;

public class InvalidQuestionStateException extends RuntimeException {
    public InvalidQuestionStateException(String message) {
        super(message);
    }
}
