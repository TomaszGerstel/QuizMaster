package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

public class ManualScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(
            EvalQuestion question,
            QuestionSolution solution
    ) {
        return new EvaluationResult(false, 0, 1, null);
    }
}
