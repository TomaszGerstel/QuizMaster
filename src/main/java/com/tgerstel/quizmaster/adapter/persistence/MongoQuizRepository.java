package com.tgerstel.quizmaster.adapter.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoQuizRepository extends MongoRepository<QuizDocument, ObjectId> {
    boolean existsByTitle(String name);
    List<QuizDocument> findAllByEditable(boolean editable);
}
