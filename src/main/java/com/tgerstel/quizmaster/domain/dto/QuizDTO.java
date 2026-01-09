package com.tgerstel.quizmaster.domain.dto;

import java.util.List;

public record QuizDTO(
        String id,
        String name,
        List<QuestionDTO> questions
) {


}
