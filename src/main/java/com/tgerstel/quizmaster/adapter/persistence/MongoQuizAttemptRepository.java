package com.tgerstel.quizmaster.adapter.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoQuizAttemptRepository extends MongoRepository<QuizAttemptDocument, String> {
    Optional<QuizAttemptDocument> findBySessionId(String sessionId);
}

