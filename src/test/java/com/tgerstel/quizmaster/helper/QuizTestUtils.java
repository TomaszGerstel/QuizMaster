package com.tgerstel.quizmaster.helper;


import com.tgerstel.quizmaster.domain.model.Answer;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.model.Quiz;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;

import java.util.ArrayList;
import java.util.List;

public class QuizTestUtils {

    public static void createAndSaveQuiz(InMemoryQuizRepositoryImpl quizRepository, String id, String title) {
        Quiz quiz = new Quiz(
           id, title, "description", "test_author", "", 1L,
           createQuestions(), Quiz.Type.EXAM, Quiz.Status.PUBLISHED, Quiz.Visibility.PUBLIC, 70,
                true, null, null, null
        );
        quizRepository.save(quiz);
    }

    public static List<Question> createQuestions() {
        List<Question> questions = new ArrayList<>();

        Question question1 = new Question(
                "some_id_1", "507f1f77bcf86cd799439011", "What is the capital of France?",
                "Paris is the capital and most populous city of France.", "capitals", "author_1",
                Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                List.of(new Answer(1, "London", false),
                        new Answer(2, "Paris", true),
                        new Answer(3, "Madrid", false)),
                1L);

        Question question2 = new Question(
                "some_id_2", "507f1f77bcf86cd799439012", "What is the capital of Germany?",
                "Berlin is the capital and largest city of Germany.", "capitals", "author_1",
                Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                Question.Status.PUBLISHED, Question.Visibility.PUBLIC,
                List.of(new Answer(1, "London", false),
                        new Answer(2, "Berlin", true),
                        new Answer(3, "Madrid", false)),
                        1L);

        questions.add(question1);
        questions.add(question2);
        return questions;
    }

    public static void createAndSaveQuizAttempt(
            InMemoryQuizAttemptRepository repository,
            String quizId,
            Long quizVersion,
            String name,
            String description,
            String sessionId,
            String userName,
            String userEmail) {

        QuizAttempt attempt = QuizAttempt.startAttempt(
                sessionId,
                quizId,
                name,
                description,
                quizVersion,
                70,
                createQuestions(),
                userName,
                userEmail
        );

        repository.create(attempt);
    }
}