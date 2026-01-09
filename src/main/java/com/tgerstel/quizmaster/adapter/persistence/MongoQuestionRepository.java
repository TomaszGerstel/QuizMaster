package com.tgerstel.quizmaster.adapter.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoQuestionRepository extends MongoRepository<QuestionDocument, ObjectId> {
    Optional<QuestionDocument> findByTagsContaining(String tag);
}
