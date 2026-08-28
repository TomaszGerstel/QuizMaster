package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.Question;

import java.math.BigDecimal;
import java.util.List;

public record QuestionDTO(
        String id,
        String question,
        String tags,
        String explanation,

        Question.Type type,
        Question.ScoringStrategyType scoringStrategyType,

        String author,
        Long version,

        List<AnswerDTO> answers,

        String expectedAnswer,
        BigDecimal correctNumber,
        BigDecimal tolerance,
        Boolean correctBoolean,

        Integer ratingMin,
        Integer ratingMax
) {
}
