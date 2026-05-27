package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    List<QuizBasicDTO> getAll();
    List<QuizDTO> getAllEditableQuizzesDetailed();
    Optional<QuizToSolveDTO> getById(String id);
    Optional<QuizEvalDTO> getEvalById(String id);
    String createQuiz(CreateQuizCommand command);
    void addQuestionsToQuiz(String quizId, List<String> ids);
    void removeQuestionsFromQuiz(String quizId, List<String> ids);
    boolean quizExistsByName(String name);
}
