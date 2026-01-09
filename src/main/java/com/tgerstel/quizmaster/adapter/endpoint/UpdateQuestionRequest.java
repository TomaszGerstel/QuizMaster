package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.dto.AnswerDTO;

import java.util.List;

public record UpdateQuestionRequest(
        String id,
        String question,
        List<AnswerDTO> answers,
        String explanation
) {
}
