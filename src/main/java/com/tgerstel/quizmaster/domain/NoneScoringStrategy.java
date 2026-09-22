package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;


public class NoneScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(
            EvalQuestion question,
            QuestionSolution solution
    ) {

        if(solution.isEmpty()) {
            return new EvaluationResult(false, 0, 1, null);
        }

        return new EvaluationResult(true, 1, 1, null);
    }
}
