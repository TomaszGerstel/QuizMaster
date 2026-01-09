package com.tgerstel.quizmaster.domain.command;

import com.tgerstel.quizmaster.domain.dto.AnswerDTO;

import java.util.List;

public record CreateQuestionCommand(
        String question,
        List<AnswerDTO> answers,
        String explanation,
        String tags,
        String author
) {
}
