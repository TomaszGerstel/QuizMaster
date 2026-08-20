package com.tgerstel.quizmaster.adapter.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface MongoQuestionRepository extends MongoRepository<QuestionDocument, ObjectId> {

    Optional<QuestionDocument> findTopByQuestionIdOrderByVersionDesc(String id);
    Set<QuestionDocument> findByQuestionIdIn(Set<String> ids);
    Set<QuestionDocument> findByTagsContainingIgnoreCase(String tag);
}
