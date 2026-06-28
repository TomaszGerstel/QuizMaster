package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.exception.InvalidQuestionStateException;
import com.tgerstel.quizmaster.domain.exception.QuestionNotFoundException;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.model.Quiz;
import com.tgerstel.quizmaster.domain.exception.QuizNotFoundException;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Profile({"prod", "dev", "dokploy"})
public class QuizRepositoryImpl implements QuizRepository {

    private final MongoQuizRepository mongoQuizRepository;
    private final MongoQuestionRepository questionRepository;

    public List<QuizBasicDTO> getAllQuizzesForVisibilityAndStatus(
            EnumSet<Quiz.Visibility> visibility,
            EnumSet<Quiz.Status> status
    ) {
        var found = mongoQuizRepository.findAllByVisibilityInAndStatusIn(visibility, status);
        return found.stream().map(QuizDocument::toBasicDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getAllQuizzesDetailedForVisibilityAndStatus(
            EnumSet<Quiz.Visibility> visibility,
            EnumSet<Quiz.Status> status
    ) {
        var found = mongoQuizRepository.findAllByVisibilityInAndStatusIn(visibility, status);

        return found.stream()
                .map(q -> q.toDetailedDTO(getLatestQuestions(q.questionIds)))
                .toList();
    }

    @Override
    public Optional<Quiz> getById(final String id, EnumSet<Quiz.Visibility> visibility,
                                  EnumSet<Quiz.Status> status) {
        var found = mongoQuizRepository.findById(new ObjectId(id));
        if (found.isEmpty()) return Optional.empty();
        var latestQuestions = getLatestQuestions(found.get().getQuestionIds());
        return found.map(d -> d.toDomain(latestQuestions));
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        var quizDocument = new QuizDocument();
        quizDocument.setTitle(command.name());
        quizDocument.setAuthor(command.author());

        quizDocument.setPassRate(70);
        quizDocument.setShuffleQuestions(true);
        quizDocument.setType(Quiz.Type.EXAM);
        quizDocument.setStatus(Quiz.Status.PUBLISHED);
        quizDocument.setVisibility(Quiz.Visibility.PUBLIC);

//        addQuestionsToQuiz(quizDocument, command.questionIds());

        return mongoQuizRepository.save(quizDocument).getId().toString();
    }

    @Override
    public void addQuestionsToQuiz(
            String quizId,
            Set<String> ids,
            EnumSet<Question.Status> allowedStatus,
            EnumSet<Question.Visibility> allowedVisibility
    ) {

        QuizDocument quiz = mongoQuizRepository.findById(new ObjectId(quizId))
                .orElseThrow(() -> new QuizNotFoundException(quizId));
        addQuestionsToQuiz(quiz, ids, allowedStatus, allowedVisibility);
    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, Set<String> ids) {
        QuizDocument quiz = mongoQuizRepository.findById(new ObjectId(quizId))
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        if (quiz.questionIds != null) {
            ids.forEach(quiz.getQuestionIds()::remove);
            mongoQuizRepository.save(quiz);
        }
    }

    private Set<QuestionDocument> getLatestQuestions(Set<String> questionIds) {
        return questionRepository
                .findByQuestionIdIn(questionIds)
                .stream()
                .collect(Collectors.groupingBy(QuestionDocument::getQuestionId))
                .values()
                .stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(QuestionDocument::getVersion))
                        .orElseThrow())
                .collect(Collectors.toSet());
    }

    private void addQuestionsToQuiz(QuizDocument quiz,
                                    Set<String> ids,
                                    EnumSet<Question.Status> allowedStatus,
                                    EnumSet<Question.Visibility> allowedVisibility) {

        validateQuestions(ids, allowedStatus, allowedVisibility);

        if (quiz.questionIds == null) {
            quiz.setQuestionIds(new HashSet<>());
        }
        quiz.questionIds.addAll(ids);

        mongoQuizRepository.save(quiz);
    }

    @Override
    public boolean quizExistsByName(String name) {
        return mongoQuizRepository.existsByTitle(name);
    }

    private void validateQuestions(
            Set<String> ids,
            EnumSet<Question.Status> allowedStatus,
            EnumSet<Question.Visibility> allowedVisibility
    ) {
        var questions = getLatestQuestions(ids);

        if (questions.size() != ids.size()) {
            throw new QuestionNotFoundException();
        }

        var invalidQuestions = questions.stream()
                .filter(q ->
                        !allowedStatus.contains(q.getStatus()) ||
                                !allowedVisibility.contains(q.getVisibility())
                )
                .map(QuestionDocument::getQuestionId)
                .toList();

        if (!invalidQuestions.isEmpty()) {
            throw new InvalidQuestionStateException(invalidQuestions.toString());
        }

    }

}