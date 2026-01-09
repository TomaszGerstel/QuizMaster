package com.tgerstel.quizmaster.domain.command;

import org.bson.types.ObjectId;

public record StartQuizCommand(ObjectId quizId, String name, String email) {
}
