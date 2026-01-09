package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

public interface QuizRepository {
    List<QuizBasicDTO> getAll();
    List<QuizDTO> getAllEditableQuizzesDetailed();
    Optional<QuizToSolveDTO> getById(ObjectId id);
    Optional<QuizEvalDTO> getEvalById(ObjectId id);
    ObjectId createQuiz(CreateQuizCommand command);
    void addQuestionsToQuiz(ObjectId quizId, List<ObjectId> ids);
    boolean quizExistsByName(String name);
}
