package com.tgerstel.quizmaster.adapter.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoQuestionRepository extends MongoRepository<QuestionDocument, ObjectId> {
    List<QuestionDocument> findByTagsContaining(String tag);
}
