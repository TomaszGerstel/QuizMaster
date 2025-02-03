package com.tgerstel.quizmaster.domain.dto;

import com.tgerstel.quizmaster.domain.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuizDTO {
    private String id;
    private String title;
    private List<Question> questions;
}
