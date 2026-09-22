package com.tgerstel.quizmaster.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record EvalQuestion(
        String id,
        Question.Type type,
        Question.ScoringStrategyType scoringStrategyType,
        String explanation,
        List<EvalAnswer> answers,
        String expectedValue,
        BigDecimal tolerance
) {}