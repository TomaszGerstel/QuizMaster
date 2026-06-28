package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalAnswer;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

import java.util.Set;
import java.util.stream.Collectors;

public class PartialScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(EvalQuestion question, Set<Integer> actual) {

        Set<Integer> expected = question.answers().stream()
                .filter(EvalAnswer::isCorrect)
                .map(EvalAnswer::no)
                .collect(Collectors.toSet());

        long correctHits = actual.stream()
                .filter(expected::contains)
                .count();

        int max = expected.size();

        return new EvaluationResult(
                correctHits == max,
                (int) correctHits,
                max,
                expected
        );
    }
}