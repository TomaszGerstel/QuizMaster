package com.tgerstel.quizmaster.domain.event;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuizCompletedEventPayload extends DomainEvent.Payload {

    private String attemptId;
    private String quizId;
    private String recipientEmail;

    public QuizCompletedEventPayload(String attemptId, String quizId, String recipientEmail) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.recipientEmail = recipientEmail;
    }

}

