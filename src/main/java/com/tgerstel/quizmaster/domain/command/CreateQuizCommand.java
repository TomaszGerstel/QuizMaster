package com.tgerstel.quizmaster.domain.command;

import com.tgerstel.quizmaster.domain.model.Quiz;

import java.util.List;

public record CreateQuizCommand(
        String name,
        String description,
        Quiz.Type type,
        Integer passRate,
        String author,
        List<String> questionIds
) {
}
