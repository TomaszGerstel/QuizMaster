package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.model.QuestionSolution;

public interface ScoringStrategy {
    EvaluationResult evaluate(EvalQuestion question, QuestionSolution solution);
}