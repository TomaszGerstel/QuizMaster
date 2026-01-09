package com.tgerstel.quizmaster.adapter.endpoint;

import org.bson.types.ObjectId;

import java.util.List;

public record AssignQuestionsRequest(
        ObjectId quizId,
        List<ObjectId> questionIds
) {
}
