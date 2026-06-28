package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.command.StartQuizCommand;
import com.tgerstel.quizmaster.domain.dto.*;
import com.tgerstel.quizmaster.domain.exception.QuestionNotFoundException;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.model.QuestionMode;
import com.tgerstel.quizmaster.domain.model.QuizAttempt;
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
        var filter = QuizFilter.assignable();
        return quizRepository.getAllQuizzesForVisibilityAndStatus(filter.visibilities(), filter.statuses()).stream()
                .filter(q -> q.getQuestionsQuantity() > 0).toList();
    }

    @Override
    public List<QuizDTO> getAllEditableQuizzesDetailed() {
        log.info("Fetching all editable quizzes with details");
        var filter = QuizFilter.editable();
        return quizRepository.getAllQuizzesDetailedForVisibilityAndStatus(filter.visibilities(), filter.statuses());
    }

    @Override
    public QuizToTakeDTO startQuiz(StartQuizCommand command) {
        var filter = QuizFilter.assignable();
        var id = command.quizId();
        var quiz = quizRepository.getById(id, filter.visibilities(), filter.statuses())
                .orElseThrow(() -> new QuizNotFoundException(id));
        var sessionId = UUID.randomUUID().toString();

        attemptRepository.create(QuizAttempt.startAttempt(
                sessionId,
                id,
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getVersion(),
                quiz.getPassRate(),
                quiz.getQuestions(),
                command.name(),
                command.email()));

        log.info("Starting quiz with ID: {} for session: {} and username: {}", id, sessionId, command.name());
        return quiz.toTakeDTO(sessionId);
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
    public List<QuestionDTO> getQuestions(String tag, QuestionMode mode) {
        log.info("Fetching all questions");

        var filter = switch (mode) {
            case ASSIGNABLE -> QuestionFilter.assignable();
            case EDITABLE -> QuestionFilter.editable();
        };

        return tag == null
                ? questionRepository.getAllInLatestVersions(
                filter.statuses(),
                filter.visibilities()
        )
                : questionRepository.getForTagInLatestVersions(
                tag,
                filter.statuses(),
                filter.visibilities()
        );
    }

    @Override
    public String createQuestion(CreateQuestionCommand command) {
        log.info("Creating new question with text: {}", command.question());
        var questionId = UUID.randomUUID().toString();
        create(command, questionId, 1L);
        return questionId;
    }

    @Override
    public void updateQuestion(String id, CreateQuestionCommand command) {
        log.info("Updating question with ID: {}", id);

        var filter = QuestionFilter.editable();

        var question = questionRepository
                .findByIdStatusAndVisibility(id, filter.statuses(), filter.visibilities())
                .orElseThrow(() -> new QuestionNotFoundException(id));
        var latestVersion = question.version();
        create(command, id, latestVersion + 1);
    }

    private void create(CreateQuestionCommand command, String questionId, Long version) {
        var recordId = questionRepository.createQuestion(command, questionId, version);
        log.info("Question created with ID: {}, and record ID {}", questionId, recordId);
    }

    @Override
    public void assignQuestionsToQuiz(String quizId, Set<String> ids) {
        log.info("Assigning questions to quiz with ID: {}", quizId);

        var filter = QuestionFilter.assignable();

        quizRepository.addQuestionsToQuiz(quizId, ids, filter.statuses(), filter.visibilities());
    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, Set<String> ids) {
        log.info("Removing questions from quiz with ID: {}", quizId);
        quizRepository.removeQuestionsFromQuiz(quizId, ids);
    }

}











