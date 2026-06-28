package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;

import java.util.Set;

public interface ScoringStrategy {
    EvaluationResult evaluate(EvalQuestion question, Set<Integer> actualAnswers);
}