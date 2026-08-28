package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizBasicDTO {
    private String id;
    private String title;
    private String description;
    private Integer questionsQuantity;
    private Quiz.Type type;
}
