package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;

import java.util.List;

public interface QuizManager {
    List<QuizBasicDTO> getAllQuizzes();
    QuizDTO getQuiz(String id);
}
