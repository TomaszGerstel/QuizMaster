package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

import java.util.Set;

public class NoneScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(EvalQuestion question, Set<Integer> actual) {
        return new EvaluationResult(true, 0, 0, null);
    }
}
