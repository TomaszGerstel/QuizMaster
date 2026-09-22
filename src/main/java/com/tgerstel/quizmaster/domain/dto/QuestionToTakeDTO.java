package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.Question;

import java.util.List;

public record QuestionToTakeDTO(
        String questionId,
        String question,
        Question.Type type,
        Question.ScoringStrategyType scoringStrategyType,
        List<AnswerToTakeDTO> answers
) {
}
