package com.tgerstel.quizmaster.domain.command;

import org.bson.types.ObjectId;

import java.util.List;

public record CreateQuizCommand(
        String name,
        String author,
        List<ObjectId> questionIds
) {
}
