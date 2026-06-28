package com.tgerstel.quizmaster.domain.dto;

import java.util.List;

public record QuestionToTakeDTO(
        String questionId,
        String question,
        List<AnswerToTakeDTO> answers
) {
}
