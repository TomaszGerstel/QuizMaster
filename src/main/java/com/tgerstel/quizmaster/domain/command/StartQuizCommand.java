package com.tgerstel.quizmaster.domain.command;

public record StartQuizCommand(String quizId, String name, String email) {
}
