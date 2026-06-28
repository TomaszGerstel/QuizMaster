package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.model.Quiz;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface MongoQuizRepository extends MongoRepository<QuizDocument, ObjectId> {
    boolean existsByTitle(String name);
    List<QuizDocument> findAllByVisibilityInAndStatusIn(
            Collection<Quiz.Visibility> visibility,
            Collection<Quiz.Status> status
    );
}
