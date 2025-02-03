package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.model.QuizResult;
import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;

public interface QuizEvaluator {
    QuizResult submitQuiz(SubmitQuizCommand command);
}
