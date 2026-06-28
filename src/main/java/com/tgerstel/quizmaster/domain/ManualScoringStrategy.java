package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

import java.util.Set;

public class ManualScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(EvalQuestion question, Set<Integer> actual) {
        return new EvaluationResult(false, 0, 1, null);
    }
}
