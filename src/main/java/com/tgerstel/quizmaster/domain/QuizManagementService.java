package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.command.StartQuizCommand;
import com.tgerstel.quizmaster.domain.dto.*;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import com.tgerstel.quizmaster.domain.port.QuizAttemptRepository;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import com.tgerstel.quizmaster.domain.port.QuizManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class QuizManagementService implements QuizManager {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;

    @Override
    public List<QuizBasicDTO> getAllQuizzes() {
        log.info("Fetching all quizzes");
        return quizRepository.getAll().stream()
                .filter(q -> q.getQuestionsQuantity() > 0).toList();
    }

    @Override
    public List<QuizDTO> getAllEditableQuizzesDetailed() {
        log.info("Fetching all editable quizzes with details");
        return quizRepository.getAllEditableQuizzesDetailed();
    }

    @Override
    public QuizToSolveDTO startQuiz(StartQuizCommand command) {
        var id = command.quizId();
        var quiz = quizRepository.getById(id).orElseThrow(() -> new QuizNotFoundException(id));
        var sessionId = UUID.randomUUID().toString();
        quiz.setSessionId(sessionId);
        attemptRepository.create(QuizAttemptDTO.startAttempt(sessionId, id, quiz.getQuestions().size(), command.name(),
                command.email()));
        log.info("Starting quiz with ID: {} for session: {} and username: {}", id, sessionId, command.name());
        return quiz;
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        log.info("Creating new quiz with name: {}", command.name());
        if (quizRepository.quizExistsByName(command.name())) {
            log.warn("Quiz creation failed: Quiz with name '{}' already exists.", command.name());
            throw new IllegalArgumentException("Quiz with name '" + command.name() + "' already exists.");
        }
        var created = quizRepository.createQuiz(command);
        log.info("Quiz created with ID: {}", created);
        return created;
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        log.info("Fetching all questions");
        return questionRepository.getAll();
    }

    @Override
    public List<QuestionDTO> getQuestionsForTag(String tag) {
        log.info("Fetching questions for tag: {}", tag);
        return questionRepository.getForTag(tag);
    }

    @Override
    public String createQuestion(CreateQuestionCommand command) {
        log.info("Creating new question with text: {}", command.question());
        var id = UUID.randomUUID().toString();
        questionRepository.createQuestion(command, id);
        log.info("Question created with ID: {}", id);
        return id;
    }

    @Override
    public void assignQuestionsToQuiz(String quizId, List<String> ids) {
        log.info("Assigning questions to quiz with ID: {}", quizId);
        quizRepository.addQuestionsToQuiz(quizId, ids);

    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, List<String> ids) {
        log.info("Removing questions from quiz with ID: {}", quizId);
        quizRepository.removeQuestionsFromQuiz(quizId, ids);
    }

}











