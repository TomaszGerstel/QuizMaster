package com.tgerstel.quizmaster.helper;


import com.tgerstel.quizmaster.adapter.persistence.BaseAnswer;
import com.tgerstel.quizmaster.adapter.persistence.QuestionDocument;
import com.tgerstel.quizmaster.adapter.persistence.QuizDocument;
import com.tgerstel.quizmaster.domain.dto.QuizAttemptDTO;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class QuizTestUtils {

    public static void createAndSaveQuizDocument(InMemoryQuizRepositoryImpl quizRepository, String id, String title) {
        QuizDocument quiz = new QuizDocument();
        quiz.setId(id);
        quiz.setTitle(title);
        quiz.setQuestions(createQuestions());
        quizRepository.save(quiz);
    }

    public static List<QuestionDocument> createQuestions() {
        List<QuestionDocument> questions = new ArrayList<>();

        QuestionDocument question1 = new QuestionDocument();
        question1.setId("1");
        question1.setQuestion("What is the capital of France?");
        question1.setAnswers(List.of(
                createAnswer("London", false, 1),
                createAnswer("Paris", true, 2),
                createAnswer("Madrid", false, 3)));
        questions.add(question1);

        QuestionDocument question2 = new QuestionDocument();
        question2.setId("2");
        question2.setQuestion("What is the capital of Germany?");
        question2.setAnswers(List.of(
                createAnswer("London", false, 1),
                createAnswer("Berlin", true, 2),
                createAnswer("Madrid", false, 3)));
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

    public static void createAndSaveQuizAttempt(InMemoryQuizAttemptRepository repository, String quizId, String sessionId) {
        QuizAttemptDTO attempt = new QuizAttemptDTO(sessionId, quizId, Instant.now().minusSeconds(90), null);
        repository.create(attempt);
    }
}