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
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class QuizManagementService implements QuizManager {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuestionRepository questionRepository;

    @Override
    public List<QuizBasicDTO> getAllQuizzes() {
        return quizRepository.getAll().stream()
                .filter(q -> q.getQuestionsQuantity() > 0).toList();
    }

    @Override
    public List<QuizDTO> getAllEditableQuizzesDetailed() {
        return quizRepository.getAllEditableQuizzesDetailed();
    }

    @Override
    public QuizToSolveDTO startQuiz(StartQuizCommand command) {
        var id = command.quizId();
        var quiz = quizRepository.getById(id).orElseThrow(() -> new QuizNotFoundException(id.toString()));
        var sessionId = UUID.randomUUID().toString();
        quiz.setSessionId(sessionId);
        attemptRepository.create(QuizAttemptDTO.startAttempt(sessionId, id, quiz.getQuestions().size(), command.name(),
                command.email()));
        return quiz;
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        if (quizRepository.quizExistsByName(command.name())) {
            throw new IllegalArgumentException("Quiz with name '" + command.name() + "' already exists.");
        }
        return quizRepository.createQuiz(command).toString();
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.getAll();
    }

    @Override
    public List<QuestionDTO> getQuestionsForTag(String tag) {
        return questionRepository.getForTag(tag);
    }

    @Override
    public String createQuestion(CreateQuestionCommand command) {
        var id = UUID.randomUUID().toString();
        questionRepository.createQuestion(command, id);
        return id;
    }

    @Override
    public void assignQuestionsToQuiz(ObjectId quizId, List<ObjectId> ids) {
        quizRepository.addQuestionsToQuiz(quizId, ids);

    }

}











