package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;
import com.tgerstel.quizmaster.domain.exception.AttemptNotFoundException;
import com.tgerstel.quizmaster.domain.model.*;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class QuizEvaluationService implements QuizEvaluator {

    private final QuizAttemptRepository attemptRepository;
    private final ScoringStrategyFactory strategyFactory;


    private static final int QUIZ_PASS_RATE = 65;

    public QuizEvaluationService(
            QuizAttemptRepository attemptRepository,
            ScoringStrategyFactory strategyFactory
    ) {
        this.attemptRepository = attemptRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public QuizResult submitQuiz(final SubmitQuizCommand command) {
        var sessionId = command.sessionId();
        log.info("Submitting quiz attempt with ID: {}", sessionId);
        var endTime = Instant.now();

        var attempt = attemptRepository.getToEval(sessionId)
                .orElseThrow(() -> new AttemptNotFoundException(sessionId));

        List<AnswerReportEntry> report = new ArrayList<>();
        int scoreSum = 0;

        var questions = attempt.questions();
        var solutions = command.solution();

        solutions.forEach(s -> validateQuestionId(questions, s.questionId()));

        for (EvalQuestion q : questions) {
            Set<Integer> actual = solutions.stream()
                    .filter(s -> s.questionId().equals(q.id()))
                    .map(QuestionSolution::answers)
                    .findFirst()
                    .map(HashSet::new)
                    .orElse(new HashSet<>());

            var strategy = strategyFactory.get(q.scoringStrategyType());
            EvaluationResult result = strategy.evaluate(q, actual);

            report.add(new AnswerReportEntry(
                    q.id(),
                    result.expectedAnswers(),
                    q.explanation(),
                    result.correct()
            ));
            scoreSum += result.score();
        }

        int maxScore = questions.size();
        int percentage = maxScore == 0 ? 0 : (scoreSum * 100 / maxScore);
        boolean passed = percentage >= QUIZ_PASS_RATE;
        var attemptTime = getQuizTime(attempt.startTime(), endTime).getSeconds();

        attemptRepository.endAttempt(sessionId, endTime, scoreSum);

        log.info("Quiz submission completed for session: {}. Passed: {}, Score: {}/{} ({}%) in {} seconds.",
                sessionId, passed, scoreSum, maxScore, percentage, attemptTime);

        return new QuizResult(
                attempt.quizId(),
                passed,
                scoreSum,
                percentage,
                maxScore,
                report,
                attemptTime
        );
    }

    private Duration getQuizTime(Instant startTime, Instant endTime) {
        if (endTime == null || startTime == null) return null;
        return Duration.between(startTime, endTime);
    }

    private void validateQuestionId(List<EvalQuestion> questions, String questionId) {
        questions.stream()
                .filter(q -> q.id().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question with id: " + questionId + " not related to the quiz"
                ));
    }
}
