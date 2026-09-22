package com.tgerstel.quizmaster.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record QuestionSolution(
        String questionId,
        Question.Type type,
        List<Integer> answers,
        String textAnswer,
        BigDecimal numberAnswer,
        Boolean booleanAnswer,
        Integer ratingAnswer
) {

    public static QuestionSolution empty(String questionId, Question.Type type) {
        return new QuestionSolution(
                questionId,
                type,
                List.of(),
                null,
                null,
                null,
                null
        );
    }

    public boolean isEmpty() {
        return (answers == null || answers.isEmpty()) &&
                textAnswer == null &&
                numberAnswer == null &&
                booleanAnswer == null &&
                ratingAnswer == null;
    }
}
