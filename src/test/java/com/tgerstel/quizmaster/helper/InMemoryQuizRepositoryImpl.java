package com.tgerstel.quizmaster.helper;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.*;
import com.tgerstel.quizmaster.domain.model.Answer;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.model.Quiz;
import com.tgerstel.quizmaster.domain.port.QuestionRepository;
import com.tgerstel.quizmaster.domain.port.QuizRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Profile("test")
@Primary
public class InMemoryQuizRepositoryImpl implements QuizRepository {

    private final QuestionRepository inMemoryQuestionRepository;

    private final List<Quiz> quizzes = new ArrayList<>();

    public InMemoryQuizRepositoryImpl(QuestionRepository questionRepository) {
        this.inMemoryQuestionRepository = questionRepository;
    }

    @Override
    public List<QuizBasicDTO> getAllQuizzesForVisibilityAndStatus(EnumSet<Quiz.Visibility> visibility, EnumSet<Quiz.Status> status) {
        return quizzes.stream()
                .filter(q -> visibility.contains(q.getVisibility()) && status.contains(q.getStatus()))
                .map(q -> new QuizBasicDTO(q.getId(), q.getTitle(), q.getQuestions().size()))
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDTO> getAllQuizzesDetailedForVisibilityAndStatus(EnumSet<Quiz.Visibility> visibility, EnumSet<Quiz.Status> status) {
        return quizzes.stream()
                .filter(q -> visibility.contains(q.getVisibility()) && status.contains(q.getStatus()))
                .map(q -> new QuizDTO(q.getId(), q.getTitle(), toQuestionDtos(q.getQuestions()))).toList();
    }

    private List<QuestionDTO> toQuestionDtos(List<Question> questions) {
        return questions.stream().map(this::toQuestionDto).toList();
    }

    private QuestionDTO toQuestionDto(Question q) {
        var mappedAnswers = q.answers().stream().map(this::toAnswerDTO).toList();
        return new QuestionDTO(q.id(), q.question(), q.tags(), q.explanation(), q.author(), q.version(), mappedAnswers);
    }

    private List<Answer> toAnswers(List<AnswerDTO> answers) {
        var no = 1;
        List<Answer> mapped = new ArrayList<>();
        for(AnswerDTO a : answers) {
            mapped.add(new Answer(no, a.value(), a.isCorrect()));
            no++;
        }
        return mapped;
    }

    private List<AnswerDTO> toAnswerDTOs(List<Answer> a) {
        return a.stream().map(this::toAnswerDTO).toList();
    }

    private AnswerDTO toAnswerDTO(Answer a) {
        return new AnswerDTO(a.content(), a.correct());
    }

    @Override
    public Optional<Quiz> getById(String id,
                                  EnumSet<Quiz.Visibility> visibility,
                                  EnumSet<Quiz.Status> status
    ) {
        return quizzes.stream()
                .filter(quiz -> quiz.getId().equals(id))
                .findFirst();
    }

    @Override
    public String createQuiz(CreateQuizCommand command) {
        var id = UUID.randomUUID().toString();
        var quiz = new Quiz(id, command.name(), "description", command.author(), "ownerId", 1L,
                null, Quiz.Type.EXAM, Quiz.Status.PUBLISHED , Quiz.Visibility.PUBLIC, 70, true,
                null, null, null);

        quizzes.add(quiz);
        return id;
    }

    @Override
    public void addQuestionsToQuiz(
            String quizId,
            Set<String> ids,
            EnumSet<Question.Status> allowedStatus,
            EnumSet<Question.Visibility> allowedVisibility
            ) {
        Quiz quiz = quizzes.stream()
                .filter(q -> q.getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz with id '" + quizId + "' not found."));

        List<Question> questions = new ArrayList<>();
        for (String questionId : ids) {
            var q = inMemoryQuestionRepository.findByIdStatusAndVisibility(questionId,
                           EnumSet.of(Question.Status.PUBLISHED), EnumSet.of(Question.Visibility.PUBLIC))
                    .orElseThrow(() -> new IllegalArgumentException("Question with id '" + questionId + "' not found."));
            questions.add(new Question(null, q.id(), q.question(), q.explanation(), q.tags(), q.author(),
                    Question.Type.MULTIPLE_CHOICE, Question.ScoringStrategyType.ALL_OR_NOTHING,
                    Question.Status.PUBLISHED, Question.Visibility.PUBLIC, toAnswers(q.answers()), q.version()));
        }

        if (quiz.getQuestions() == null) {
            quiz.setQuestions(questions);
        } else {
            quiz.getQuestions().addAll(questions);
        }
    }

    @Override
    public void removeQuestionsFromQuiz(String quizId, Set<String> ids) {
        Quiz quiz = quizzes.stream()
                .filter(q -> q.getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Quiz with id '" + quizId + "' not found."));

        if (quiz.getQuestions() != null) {
            quiz.getQuestions().removeIf(question -> ids.contains(question.questionId()));
        }
    }

    @Override
    public boolean quizExistsByName(String name) {
        return quizzes.stream()
                .anyMatch(quiz -> quiz.getTitle().equals(name));
    }

    public void save(Quiz quiz) {
        quizzes.add(quiz);
    }

    public void clear() {
        quizzes.clear();
    }
}

