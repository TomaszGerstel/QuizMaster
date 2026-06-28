package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.domain.dto.AttemptToEvalDTO;
import com.tgerstel.quizmaster.domain.model.*;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@Profile("test")
@Primary
public class InMemoryQuizAttemptRepository implements QuizAttemptRepository {

    private final List<QuizAttempt> attempts = new ArrayList<>();

    private final InMemoryQuestionRepository questionRepository = new InMemoryQuestionRepository();

    @Override
    public void create(QuizAttempt attempt) {

        var ids = attempt.getQuestions().stream().map(Question::questionId).toList();
        attempts.add(attempt);

        var questions = questionRepository.getAllInLatestVersions(
                EnumSet.of(Question.Status.PUBLISHED), EnumSet.of(Question.Visibility.SYSTEM)
        ).stream().filter(q -> ids.contains(q.id()));
    }

    @Override
    public Optional<AttemptToEvalDTO> getToEval(String sessionId) {
         var foundOpt = attempts.stream().filter(a -> a.getSessionId().equals(sessionId)).findFirst();
         if (foundOpt.isEmpty()) return Optional.empty();
         var found = foundOpt.get();

         return Optional.of(new AttemptToEvalDTO(
                 found.getSessionId(), found.getQuizId(), found.getStartTime(), mapToEvalQuestions(found.getQuestions())
                 ));
    }

    private List<EvalQuestion> mapToEvalQuestions(List<Question> in) {
        var questions = new ArrayList<EvalQuestion>();
        for(Question q : in) {
            questions.add(new EvalQuestion(
                    q.id(), q.type(), q.scoringStrategyType(), q.explanation(), mapToEvalAnswers(q.answers())
            ));
        }
        return questions;
    }

    private List<EvalAnswer> mapToEvalAnswers(List<Answer> in) {
        var answers = new ArrayList<EvalAnswer>();
        for(Answer a : in) {
            answers.add(new EvalAnswer(a.no(), a.correct()));
        }
        return answers;
    }

    @Override
    public void endAttempt(String sessionId, Instant endTime, int score) {

        var attemptOpt = attempts.stream().filter(a -> Objects.equals(a.getSessionId(), sessionId)).findFirst();

        if (attemptOpt.isEmpty()) return;

        var attempt = attemptOpt.get();
        attempt.setEndTime(endTime);
        attempt.setCorrectAnswers(score);
    }

    public void clear() {
        attempts.clear();
    }
}
