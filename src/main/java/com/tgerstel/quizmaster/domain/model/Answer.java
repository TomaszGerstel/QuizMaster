package com.tgerstel.quizmaster.domain.model;

import com.tgerstel.quizmaster.domain.dto.AnswerToTakeDTO;

public record Answer(
        int no,
        String content,
        boolean correct
) {
    public AnswerToTakeDTO toTakeDTO() {
        return new AnswerToTakeDTO(no, content);
    }
}
