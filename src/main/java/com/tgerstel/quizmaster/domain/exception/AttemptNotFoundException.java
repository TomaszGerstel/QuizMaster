package com.tgerstel.quizmaster.domain.exception;

public class AttemptNotFoundException extends RuntimeException {

    public AttemptNotFoundException(String sessionId) {
        super("Attempt with ID " + sessionId + " not found");
    }
}
