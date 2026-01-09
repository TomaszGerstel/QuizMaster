package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.command.StartQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import org.bson.types.ObjectId;

import java.util.List;

public interface QuizManager {
    List<QuizBasicDTO> getAllQuizzes();
    List<QuizDTO> getAllEditableQuizzesDetailed();
    QuizToSolveDTO startQuiz(StartQuizCommand command);
    String createQuiz(CreateQuizCommand title);
    List<QuestionDTO> getAllQuestions();
    List<QuestionDTO> getQuestionsForTag(String tag);
    String createQuestion(CreateQuestionCommand command);
    void assignQuestionsToQuiz(ObjectId quizId, List<ObjectId> ids);
}
