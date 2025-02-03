package com.tgerstel.quizmaster.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizBasicDTO {
    private String id;
    private String title;
    private Integer questionsQuantity;
}
