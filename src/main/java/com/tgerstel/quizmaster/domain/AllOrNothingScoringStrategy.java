package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalAnswer;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class AllOrNothingScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(EvalQuestion question, Set<Integer> actual) {

        Set<Integer> expected = question.answers().stream()
                .filter(EvalAnswer::isCorrect)
                .map(EvalAnswer::no)
                .collect(Collectors.toSet());

        boolean correct = !actual.isEmpty() && expected.equals(actual);

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                expected
        );
    }
}
