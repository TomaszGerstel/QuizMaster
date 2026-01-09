package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.command.StartQuizCommand;
import org.bson.types.ObjectId;

public record StartQuizRequest(ObjectId quizId, String name, String email) {
    public StartQuizCommand toCommand() {
        return new StartQuizCommand(quizId, name, email);
    }
}
