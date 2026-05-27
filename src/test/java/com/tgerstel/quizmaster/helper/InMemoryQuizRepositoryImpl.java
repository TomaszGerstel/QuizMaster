package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.adapter.persistence.QuestionDocument;
import com.tgerstel.quizmaster.adapter.persistence.QuizDocument;
import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.*;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("test")
@Primary
public class InMemoryQuizRepositoryImpl implements QuizRepository {

    private final QuestionRepository inMemoryQuestionRepository;

    private final List<QuizDocument> quizDocuments = new ArrayList<>();

    public InMemoryQuizRepositoryImpl(QuestionRepository questionRepository) {
        this.inMemoryQuestionRepository = questionRepository;
    }

    @Override
    public List<QuizBasicDTO> getAll() {
        return quizDocuments.stream()
                .map(QuizDocument::toBasicDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getAllEditableQuizzesDetailed() {
        return quizDocuments.stream().filter(QuizDocument::isEditable)
                .map(QuizDocument::toDetailedDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<QuizToSolveDTO> getById(String id) {
        return quizDocuments.stream()
                .filter(quiz -> quiz.getId().toString().equals(id))
                .findFirst()
                .map(QuizDocument::toSolveDTO);
    }

    @Override
    public Optional<QuizEvalDTO> getEvalById(String id) {
        return quizDocuments.stream()
                .filter(quiz -> quiz.getId().toString().equals(id))
                .findFirst()
                .map(QuizDocument::toEvalDTO);
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        var quizDocument = new QuizDocument();
        ObjectId id = new ObjectId();
        quizDocument.setId(id);
        quizDocument.setTitle(command.name());
        quizDocument.setEditable(true);
        quizDocuments.add(quizDocument);
        return id.toString();
    }

    @Override
    public void addQuestionsToQuiz(String quizId, List<String> ids) {
        QuizDocument quiz = quizDocuments.stream()
                .filter(q -> q.getId().toString().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz with id '" + quizId + "' not found."));

        var questions = new ArrayList<QuestionDTO>();
        for (String questionId : ids) {
            var question = inMemoryQuestionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question with id '" + questionId + "' not found."));
            questions.add(question);
        }

        var questionDocuments = questions.stream()
                .map(q -> {
                    var questionDoc = new QuestionDocument();
                    questionDoc.setId(new ObjectId(q.id()));
                    questionDoc.setQuestion(q.question());
                    questionDoc.setTags(q.tags());
                    questionDoc.setAuthor(q.author());
                    questionDoc.setExplanation(q.explanation());
                    questionDoc.setTags(q.tags());
                    questionDoc.setAnswers(q.answers().stream().map(a -> {
                        var baseAnswer = new com.tgerstel.quizmaster.adapter.persistence.BaseAnswer();
                        baseAnswer.setNo(q.answers().indexOf(a) + 1);
                        baseAnswer.setValue(a.value());
                        baseAnswer.setCorrect(a.isCorrect());
                        return baseAnswer;
                    }).collect(Collectors.toList()));
                    return questionDoc;
                })
                .collect(Collectors.toSet());

        if (quiz.getQuestions() == null) {
            quiz.setQuestions(questionDocuments);
        } else {
            quiz.getQuestions().addAll(questionDocuments);
        }
    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, List<String> ids) {
        QuizDocument quiz = quizDocuments.stream()
                .filter(q -> q.getId().toString().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz with id '" + quizId + "' not found."));

        if (quiz.getQuestions() != null) {
            quiz.getQuestions().removeIf(question -> ids.contains(question.getId().toString()));
        }
    }

    @Override
    public boolean quizExistsByName(String name) {
        return quizDocuments.stream()
                .anyMatch(quiz -> quiz.getTitle().equals(name));
    }

    public void save(QuizDocument quizDocument) {
        quizDocuments.add(quizDocument);
    }

    public void clear() {
        quizDocuments.clear();
    }
}

