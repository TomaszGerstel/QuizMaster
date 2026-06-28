package com.tgerstel.quizmaster.adapter.endpoint;

import java.util.Set;

public record AssignQuestionsRequest(
        String quizId,
        Set<String> questionIds
) {
}
