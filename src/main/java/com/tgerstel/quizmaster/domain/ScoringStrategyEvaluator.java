package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.Question;

import java.util.List;

public class ScoringStrategyEvaluator {

    public static List<Question.ScoringStrategyType> getAllowedScoringStrategies(Question.Type type) {
        return switch (type) {
            case SINGLE_CHOICE, NUMBER, BOOLEAN ->
                    List.of(Question.ScoringStrategyType.ALL_OR_NOTHING);

            case MULTIPLE_CHOICE ->
                    List.of(Question.ScoringStrategyType.ALL_OR_NOTHING,
                            Question.ScoringStrategyType.PARTIAL);

            case TEXT ->
                    List.of(Question.ScoringStrategyType.MANUAL);

            case RATING, SURVEY ->
                    List.of(Question.ScoringStrategyType.NONE);
        };
    }

    public static void validate(Question.Type type,
                                Question.ScoringStrategyType scoringStrategyType) {
        var allowed = getAllowedScoringStrategies(type);

        if (!allowed.contains(scoringStrategyType)) {
            throw new IllegalArgumentException(
                    "Scoring strategy " + scoringStrategyType
                            + " is not allowed for question type "
                            + type
            );
        }
    }

}
