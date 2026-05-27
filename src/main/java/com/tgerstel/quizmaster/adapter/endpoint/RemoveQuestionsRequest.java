package com.tgerstel.quizmaster.adapter.endpoint;


import java.util.List;

public record RemoveQuestionsRequest(
        String quizId,
        List<String> questionIds
) {

}
