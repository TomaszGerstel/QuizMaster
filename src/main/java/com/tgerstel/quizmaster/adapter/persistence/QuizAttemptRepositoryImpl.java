package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.AttemptToEvalDTO;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Profile({"prod", "dev", "dokploy"})
public class QuizAttemptRepositoryImpl implements QuizAttemptRepository {

    MongoQuizAttemptRepository mongoRepository;
    MongoQuestionRepository questionRepository;

    @Override
    public void create(QuizAttempt attempt) {
        var questions = attempt.getQuestions().stream();
        var ids = questions.map(Question::questionId).collect(Collectors.toSet());
        var questionDocuments = questionRepository
                .findByQuestionIdIn(ids)
                .stream()
                .collect(Collectors.groupingBy(QuestionDocument::getQuestionId))
                .values()
                .stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(QuestionDocument::getVersion))
                        .orElseThrow())
                .collect(Collectors.toSet());;
        mongoRepository.save(QuizAttemptDocument.initAttempt(attempt, questionDocuments));
    }

    @Override
    public Optional<AttemptToEvalDTO> getToEval(String sessionId) {
        return mongoRepository.findBySessionId(sessionId).map(QuizAttemptDocument::toEvalDTO);
    }

    @Override
    public void endAttempt(String sessionId, Instant endTime, int score) {
        var attemptOpt = mongoRepository.findBySessionId(sessionId);
        if (attemptOpt.isPresent()) {
            var a = attemptOpt.get();
            a.completeQuizAttempt(score, endTime);
            mongoRepository.save(a);
        }
    }
}
