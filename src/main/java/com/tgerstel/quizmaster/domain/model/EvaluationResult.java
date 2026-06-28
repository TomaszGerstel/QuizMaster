package com.tgerstel.quizmaster.domain.model;

import java.util.Set;

public record EvaluationResult(
        boolean correct,
        int score,
        int maxScore,
        Set<Integer> expectedAnswers
) {}