package com.tgerstel.quizmaster.adapter.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoQuizRepository extends MongoRepository<QuizDocument, String> {
}
