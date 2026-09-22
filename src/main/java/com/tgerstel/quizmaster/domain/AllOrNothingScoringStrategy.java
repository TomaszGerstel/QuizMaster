package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.EvalAnswer;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.EvaluationResult;
import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import com.tgerstel.quizmaster.domain.port.ScoringStrategy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class AllOrNothingScoringStrategy implements ScoringStrategy {

    @Override
    public EvaluationResult evaluate(
            EvalQuestion question,
            QuestionSolution solution
    ) {

        return switch (question.type()) {

            case SINGLE_CHOICE, MULTIPLE_CHOICE ->
                    evaluateChoice(question, solution);

            case NUMBER ->
                    evaluateNumber(question, solution);

            case BOOLEAN ->
                    evaluateBoolean(question, solution);

            case TEXT ->
                    evaluateText(question, solution);

            case RATING ->
                    evaluateRating(question, solution);

            case SURVEY ->
                    new EvaluationResult(true, 0, 0, null);
        };
    }

    public EvaluationResult evaluateChoice(
            EvalQuestion question,
            QuestionSolution solution
    ) {

        Set<Integer> actual = solution.answers() == null
                ? Set.of()
                : new HashSet<>(solution.answers());

        Set<Integer> expected = question.answers().stream()
                .filter(EvalAnswer::isCorrect)
                .map(EvalAnswer::no)
                .collect(Collectors.toSet());

        boolean correct = expected.equals(actual);

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                expected
        );
    }

    private EvaluationResult evaluateRating(
            EvalQuestion question,
            QuestionSolution solution
    ) {
        if (solution == null || solution.ratingAnswer() == null
                || question.expectedValue() == null) {
            return new EvaluationResult(false, 0, 1, Set.of());
        }

        int expected = Integer.parseInt(question.expectedValue());
        int actual = solution.ratingAnswer();

        boolean correct = expected == actual;

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                Set.of()
        );
    }

    private EvaluationResult evaluateText(
            EvalQuestion question,
            QuestionSolution solution
    ) {
        if (solution == null || solution.textAnswer() == null
                || question.expectedValue() == null) {
            return new EvaluationResult(false, 0, 1, Set.of());
        }

        String expected = question.expectedValue().trim();
        String actual = solution.textAnswer().trim();

        boolean correct = expected.equalsIgnoreCase(actual);

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                Set.of()
        );
    }

    private EvaluationResult evaluateBoolean(
            EvalQuestion question,
            QuestionSolution solution
    ) {
        if (solution == null || solution.booleanAnswer() == null
                || question.expectedValue() == null) {
            return new EvaluationResult(false, 0, 1, Set.of());
        }

        boolean expected = Boolean.parseBoolean(question.expectedValue());
        boolean actual = solution.booleanAnswer();

        boolean correct = expected == actual;

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                Set.of()
        );
    }

    private EvaluationResult evaluateNumber(
            EvalQuestion question,
            QuestionSolution solution
    ) {
        if (solution == null || solution.numberAnswer() == null
                || question.expectedValue() == null) {
            return new EvaluationResult(false, 0, 1, Set.of());
        }

        BigDecimal expected = new BigDecimal(question.expectedValue());
        BigDecimal actual = solution.numberAnswer();

        BigDecimal tolerance = question.tolerance() != null
                ? question.tolerance()
                : BigDecimal.ZERO;

        boolean correct = actual.subtract(expected)
                .abs()
                .compareTo(tolerance) <= 0;

        return new EvaluationResult(
                correct,
                correct ? 1 : 0,
                1,
                Set.of()
        );
    }
}
