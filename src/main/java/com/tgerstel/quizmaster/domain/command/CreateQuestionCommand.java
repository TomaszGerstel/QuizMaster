package com.tgerstel.quizmaster.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tgerstel.quizmaster.domain.dto.AnswerDTO;
import com.tgerstel.quizmaster.domain.model.Question;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateQuestionCommand(
        String question,
        List<AnswerDTO> answers,
        String explanation,
        String tags,
        String author,

        Question.Type type,
        Question.ScoringStrategyType scoringStrategyType,
        Question.Visibility visibility,
        Question.Status status
) {
}
