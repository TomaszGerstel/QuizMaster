package com.tgerstel.quizmaster.domain.dto;

import java.util.List;

public record QuizToTakeDTO(
        String title,
        String sessionId,
        String id,
        List<QuestionToTakeDTO> questions
) {
}
