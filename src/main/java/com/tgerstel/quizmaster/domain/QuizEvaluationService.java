package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;
import com.tgerstel.quizmaster.domain.command.EndAttemptCommand;
import com.tgerstel.quizmaster.domain.event.DomainEvent;
import com.tgerstel.quizmaster.domain.event.EventType;
import com.tgerstel.quizmaster.domain.event.QuizCompletedEventPayload;
import com.tgerstel.quizmaster.domain.exception.AttemptNotFoundException;
import com.tgerstel.quizmaster.domain.exception.EventPublicationException;
import com.tgerstel.quizmaster.domain.model.*;
import com.tgerstel.quizmaster.domain.port.EventPublisher;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import com.tgerstel.quizmaster.domain.port.QuizEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class QuizEvaluationService implements QuizEvaluator {

    private final QuizAttemptRepository attemptRepository;
    private final ScoringStrategyFactory strategyFactory;
    private final EventPublisher eventPublisher;


    private static final int DEFAULT_PASS_RATE = 65;

    public QuizEvaluationService(
            QuizAttemptRepository attemptRepository,
            ScoringStrategyFactory strategyFactory,
            EventPublisher eventPublisher
    ) {
        this.attemptRepository = attemptRepository;
        this.strategyFactory = strategyFactory;
        this.eventPublisher = eventPublisher;
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
        int passRate = attempt.passRate() != null ? attempt.passRate() : DEFAULT_PASS_RATE;
        boolean passed = percentage >= passRate;
        var attemptTime = getQuizTime(attempt.startTime(), endTime).getSeconds();

        var endAttempt = new EndAttemptCommand(sessionId, endTime, attemptTime, scoreSum, passRate, passed);

        attemptRepository.endAttempt(endAttempt);

        log.info("Quiz submission completed for session: {}. Passed: {}, Score: {}/{} ({}%) in {} seconds.",
                sessionId, passed, scoreSum, maxScore, percentage, attemptTime);

        var result = new QuizResult(
                attempt.quizId(),
                passed,
                scoreSum,
                percentage,
                maxScore,
                report,
                attemptTime
        );

        var eventPayload = new QuizCompletedEventPayload(
                sessionId,
                attempt.quizId(),
                attempt.email()
        );

        try {
            eventPublisher.publish(
                    new DomainEvent<>(
                            UUID.randomUUID(),
                            EventType.QUIZ_COMPLETED,
                            endTime,
                            eventPayload
                    )
            );
        } catch (EventPublicationException e) {
            log.error("Failed to publish quiz completed event for attempt: {}", attempt.sessionId(), e);
        }

        return result;
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
