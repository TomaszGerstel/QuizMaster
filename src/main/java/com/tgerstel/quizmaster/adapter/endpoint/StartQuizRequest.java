package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.command.StartQuizCommand;

public record StartQuizRequest(String quizId, String name, String email) {
    public StartQuizCommand toCommand() {
        return new StartQuizCommand(quizId, name, email);
    }
}
