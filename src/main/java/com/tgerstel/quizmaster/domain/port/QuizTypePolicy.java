package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.dto.QuestionDTO;

public interface QuizTypePolicy {
    boolean supports(QuestionDTO question);
}