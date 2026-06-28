package com.tgerstel.quizmaster.domain.port;

import com.tgerstel.quizmaster.domain.model.Question;

public interface QuizTypePolicy {
    boolean supports(Question question);
}