package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class QuizAttemptRepositoryImpl implements QuizAttemptRepository {

    MongoQuizAttemptRepository mongoRepository;

    @Override
    public void create(QuizAttemptDTO attempt) {
       mongoRepository.save(QuizAttemptDocument.initAttempt(attempt));
    }

    @Override
    public Optional<QuizAttemptDTO> getAndEnd(String sessionId, Instant endTime, int score) {
        return mongoRepository.findBySessionId(sessionId)
                .map(attempt -> {
                    attempt.completeQuizAttempt(score, endTime);
                    mongoRepository.save(attempt);
                    return attempt.toDTO();
                });
    }
}
