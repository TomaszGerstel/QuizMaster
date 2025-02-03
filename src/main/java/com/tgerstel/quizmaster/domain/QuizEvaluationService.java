package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.model.EvalAnswer;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import com.tgerstel.quizmaster.domain.model.QuizResult;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizEvaluationService implements QuizEvaluator {

    private final QuizRepository quizRepository;

    private static final int QUIZ_PASS_RATE = 70;

    public QuizEvaluationService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public QuizResult submitQuiz(SubmitQuizCommand command) {
        QuizEvalDTO quiz = quizRepository.getEvalById(command.quizId())
                .orElseThrow(() -> new QuizNotFoundException(command.quizId()));

        List<EvalQuestion> questions = quiz.questions();
        List<QuestionSolution> solutions = command.solution();

        int questionCount = questions.size();
        int correctSolutions = calculateCorrectSolutions(questions, solutions);

        final boolean evaluation = isQuizPassed(correctSolutions, questionCount);

        return new QuizResult(quiz.id(), evaluation, correctSolutions, questionCount);
    }

    private int calculateCorrectSolutions(List<EvalQuestion> questions, List<QuestionSolution> solutions) {
        int correctSolutions = 0;
        for (QuestionSolution solution : solutions) {
            var question = findQuestionById(questions, solution.questionId());
            var correctAnswers = getCorrectAnswers(question);
            var actualAnswers = getActualAnswers(solution);

            if (correctAnswers.equals(actualAnswers)) {
                correctSolutions++;
            }
        }
        return correctSolutions;
    }

    private EvalQuestion findQuestionById(List<EvalQuestion> questions, String questionId) {
        return questions.stream()
                .filter(q -> q.id().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
    }

    private List<Integer> getCorrectAnswers(EvalQuestion question) {
        return question.answers().stream()
                .filter(EvalAnswer::isCorrect)
                .map(EvalAnswer::no)
                .sorted()
                .toList();
    }

    private List<Integer> getActualAnswers(QuestionSolution solution) {
        return solution.answers().stream()
                .sorted()
                .toList();
    }

    private boolean isQuizPassed(int correctSolutions, int questionCount) {
        if (questionCount == 0) {
            throw new IllegalArgumentException("Question count cannot be zero");
        }
        return correctSolutions * 100 / questionCount >= QUIZ_PASS_RATE;
    }
}
