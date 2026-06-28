package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.model.Question;
import com.tgerstel.quizmaster.domain.model.Quiz;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuizRepository {
    List<QuizBasicDTO> getAllQuizzesForVisibilityAndStatus(EnumSet<Quiz.Visibility> visibility, EnumSet<Quiz.Status> status);
    List<QuizDTO> getAllQuizzesDetailedForVisibilityAndStatus(EnumSet<Quiz.Visibility> visibility, EnumSet<Quiz.Status> status);
    Optional<Quiz> getById(String id, EnumSet<Quiz.Visibility> visibility,
                           EnumSet<Quiz.Status> status);
    String createQuiz(CreateQuizCommand command);
    void addQuestionsToQuiz(String quizId, Set<String> ids, EnumSet<Question.Status> allowedStatus,
                            EnumSet<Question.Visibility> allowedVisibility);
    void removeQuestionsFromQuiz(String quizId, Set<String> ids);
    boolean quizExistsByName(String name);
}
