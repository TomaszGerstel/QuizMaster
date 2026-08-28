package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.Quiz;

import java.util.List;

public record QuizDTO(
        String id,
        String name,
        String description,
        String author,
        Quiz.Type type,
        Long version,
        List<QuestionDTO> questions
) {


}
