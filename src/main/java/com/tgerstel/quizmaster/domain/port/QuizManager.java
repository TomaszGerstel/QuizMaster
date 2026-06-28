package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.command.CreateQuizCommand;
import com.tgerstel.quizmaster.domain.command.StartQuizCommand;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToTakeDTO;
import com.tgerstel.quizmaster.domain.model.QuestionMode;

import java.util.List;
import java.util.Set;

public interface QuizManager {
    List<QuizBasicDTO> getAllQuizzes();
    List<QuizDTO> getAllEditableQuizzesDetailed();
    QuizToTakeDTO startQuiz(StartQuizCommand command);
    String createQuiz(CreateQuizCommand title);
    List<QuestionDTO> getQuestions(String tag, QuestionMode mode);
    String createQuestion(CreateQuestionCommand command);
    void updateQuestion(String id, CreateQuestionCommand command);
    void assignQuestionsToQuiz(String quizId, Set<String> ids);
    void removeQuestionsFromQuiz(String quizId, Set<String> ids);
}
