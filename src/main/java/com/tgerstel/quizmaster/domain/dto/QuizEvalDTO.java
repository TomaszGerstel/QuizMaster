package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;

import java.util.List;

public record QuizEvalDTO(String id, List<EvalQuestion> questions) {

}
