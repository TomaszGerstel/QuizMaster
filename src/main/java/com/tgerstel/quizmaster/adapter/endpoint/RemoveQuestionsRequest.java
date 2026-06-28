package com.tgerstel.quizmaster.adapter.endpoint;

import java.util.Set;

public record RemoveQuestionsRequest(
        String quizId,
        Set<String> questionIds
) {

}
