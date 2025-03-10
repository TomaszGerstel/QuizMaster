package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.model.*;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizEvaluationService implements QuizEvaluator {

    private final QuizRepository quizRepository;

    private static final int QUIZ_PASS_RATE = 65;

    public QuizEvaluationService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public QuizResult submitQuiz(SubmitQuizCommand command) {
        QuizEvalDTO quiz = quizRepository.getEvalById(command.quizId())
                .orElseThrow(() -> new QuizNotFoundException(command.quizId()));

        List<EvalQuestion> questions = quiz.questions();
        List<QuestionSolution> solutions = command.solution();

        solutions.forEach(s -> validateQuestionId(questions, s.questionId()));

        int questionCount = questions.size();
        List<AnswerReportEntry> answersReport = evaluateAnswers(questions, solutions);
        int correctSolutionsCount = answersReport.stream()
                .filter(AnswerReportEntry::positive)
                .toList()
                .size();

        final boolean evaluation = isQuizPassed(correctSolutionsCount, questionCount);

        return new QuizResult(quiz.id(), evaluation, correctSolutionsCount, questionCount, answersReport);
    }

    private List<AnswerReportEntry> evaluateAnswers(List<EvalQuestion> questions, List<QuestionSolution> solutions) {
        List<AnswerReportEntry> report = new ArrayList<>();
        for (EvalQuestion question : questions) {
            var expectedAnswers = getExpectedAnswers(question);
            var actualAnswers = solutions.stream()
                    .filter(s -> s.questionId().equals(question.id()))
                    .map(this::getActualAnswers)
                    .findFirst()
                    .orElse(Collections.emptySet());

            report.add(new AnswerReportEntry(question.id(), expectedAnswers,
                    isCorrectAnswer(expectedAnswers, actualAnswers)));
        }
        return report;
    }

    private boolean isCorrectAnswer(Set<Integer> expectedAnswers, Set<Integer> actualAnswers) {
        return !actualAnswers.isEmpty() && expectedAnswers.equals(actualAnswers);
    }


    private void validateQuestionId(List<EvalQuestion> questions, String questionId) {
        questions.stream()
                .filter(q -> q.id().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question with id: " + questionId + " not related to the quiz"));
    }

    private Set<Integer> getExpectedAnswers(EvalQuestion question) {
        return question.answers().stream()
                .filter(EvalAnswer::isCorrect)
                .map(EvalAnswer::no)
                .collect(Collectors.toSet());
    }

    private Set<Integer> getActualAnswers(QuestionSolution solution) {
        return new HashSet<>(solution.answers());
    }

    private boolean isQuizPassed(int correctSolutions, int questionCount) {
        if (questionCount == 0) {
            throw new IllegalArgumentException("Question count cannot be zero");
        }
        return correctSolutions * 100 / questionCount >= QUIZ_PASS_RATE;
    }

}
