package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import com.tgerstel.quizmaster.domain.exception.QuestionNotFoundException;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class QuizRepositoryImpl implements QuizRepository {

    private final MongoQuizRepository mongoQuizRepository;
    private final MongoQuestionRepository questionRepository;

    public List<QuizBasicDTO> getAll() {
        var found = mongoQuizRepository.findAll();
        return found.stream().map(QuizDocument::toBasicDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getAllEditableQuizzesDetailed() {
        return mongoQuizRepository.findAllByEditable(true).stream()
                .map(QuizDocument::toDetailedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<QuizToSolveDTO> getById(final String id) {
        var found = mongoQuizRepository.findById(new ObjectId(id));
        return found.map(QuizDocument::toSolveDTO);
    }

    @Override
    public Optional<QuizEvalDTO> getEvalById(String id) {
        var found = mongoQuizRepository.findById(new ObjectId(id));
        return found.map(QuizDocument::toEvalDTO);
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        var quizDocument = new QuizDocument();
        quizDocument.setTitle(command.name());
        quizDocument.setEditable(true);
        addQuestionsToQuiz(quizDocument, command.questionIds());
        return mongoQuizRepository.save(quizDocument).getId().toString();
    }

    @Override
    public void addQuestionsToQuiz(String quizId, List<String> ids) {
        QuizDocument quiz = mongoQuizRepository.findById(new ObjectId(quizId))
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        var objectIds = ids.stream().map(ObjectId::new).toList();
        addQuestionsToQuiz(quiz, objectIds);
    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, List<String> ids) {
        QuizDocument quiz = mongoQuizRepository.findById(new ObjectId(quizId))
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        var objectIds = ids.stream().map(ObjectId::new).toList();
        Set<QuestionDocument> questionsToRemove = new HashSet<>();
        for (ObjectId questionId : objectIds) {
            QuestionDocument question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new QuestionNotFoundException(questionId.toString()));
            questionsToRemove.add(question);
        }

        if (quiz.getQuestions() != null) {
            quiz.getQuestions().removeAll(questionsToRemove);
            mongoQuizRepository.save(quiz);
        }
    }

    private void addQuestionsToQuiz(QuizDocument quiz, List<ObjectId> ids) {
        Set<QuestionDocument> questionsToAdd = new HashSet<>();
        for (ObjectId questionId : ids) {
            QuestionDocument question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new QuestionNotFoundException(questionId.toString()));
            questionsToAdd.add(question);
        }

        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new HashSet<>());
        }
        quiz.getQuestions().addAll(questionsToAdd);

        mongoQuizRepository.save(quiz);
    }

    @Override
    public boolean quizExistsByName(String name) {
        return mongoQuizRepository.existsByTitle(name);
    }

}