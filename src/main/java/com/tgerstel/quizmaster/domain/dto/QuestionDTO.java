package com.tgerstel.quizmaster.domain.dto;

import java.util.List;

public record QuestionDTO(String id, String question, String tags, String explanation,
                          String author, Long version, List<AnswerDTO> answers) {
}
