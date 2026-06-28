package com.tgerstel.quizmaster.domain.command;

import java.util.List;

public record CreateQuizCommand(
        String name,
        String author,
        List<String> questionIds
) {
}
