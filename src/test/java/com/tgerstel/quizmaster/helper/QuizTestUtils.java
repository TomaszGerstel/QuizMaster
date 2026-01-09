package com.tgerstel.quizmaster.helper;


import com.tgerstel.quizmaster.adapter.persistence.BaseAnswer;
import com.tgerstel.quizmaster.adapter.persistence.QuestionDocument;
import com.tgerstel.quizmaster.adapter.persistence.QuizDocument;
import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizTestUtils {

    public static void createAndSaveQuizDocument(InMemoryQuizRepositoryImpl quizRepository, ObjectId id, String title) {
        QuizDocument quiz = new QuizDocument();
        quiz.setId(id);
        quiz.setTitle(title);
        quiz.setQuestions(createQuestions());
        quizRepository.save(quiz);
    }

    public static Set<QuestionDocument> createQuestions() {
        Set<QuestionDocument> questions = new HashSet<>();

        QuestionDocument question1 = new QuestionDocument();
        question1.setId(new ObjectId("507f1f77bcf86cd799439011"));
        question1.setQuestion("What is the capital of France?");
        question1.setAnswers(List.of(
                createAnswer("London", false, 1),
                createAnswer("Paris", true, 2),
                createAnswer("Madrid", false, 3)));
        question1.setExplanation("Paris is the capital and most populous city of France.");
        questions.add(question1);

        QuestionDocument question2 = new QuestionDocument();
        question2.setId(new ObjectId("507f1f77bcf86cd799439012"));
        question2.setQuestion("What is the capital of Germany?");
        question2.setAnswers(List.of(
                createAnswer("London", false, 1),
                createAnswer("Berlin", true, 2),
                createAnswer("Madrid", false, 3)));
        question2.setExplanation("Berlin is the capital and largest city of Germany.");
        questions.add(question2);
        return questions;
    }

    public static BaseAnswer createAnswer(String value, boolean correct, Integer no) {
        var baseAnswer = new BaseAnswer();
        baseAnswer.setNo(no);
        baseAnswer.setValue(value);
        baseAnswer.setCorrect(correct);
        return baseAnswer;
    }

    public static void createAndSaveQuizAttempt(InMemoryQuizAttemptRepository repository, ObjectId quizId, int questions,
                                                String sessionId, String userName, String userEmail) {
        QuizAttemptDTO attempt = new QuizAttemptDTO(sessionId, quizId.toString(), userName, userEmail,
                Instant.now().minusSeconds(90), null, questions, 0);
        repository.create(attempt);
    }
}