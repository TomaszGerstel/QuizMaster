package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Document(collection = "quiz")
@NoArgsConstructor
public class QuizDocument {
    @Id
    private String id;
    private String title;

    @DBRef
    private List<QuestionDocument> questions;

    public QuizBasicDTO toBasicDTO() {
        var count = questions.size();
        return new QuizBasicDTO(id, title, count);
    }

    public QuizDTO toDTO() {
        var mappedQuestions = questions.stream().map(QuestionDocument::toQuestion).toList();
        var shuffledQuestions = new ArrayList<>(mappedQuestions);
        Collections.shuffle(shuffledQuestions);
        return new QuizDTO(id, title, shuffledQuestions);
    }

    public QuizEvalDTO toEvalDTO() {
        var mappedQuestions = questions.stream().map(QuestionDocument::toEvalQuestion).toList();
        return new QuizEvalDTO(id, mappedQuestions);
    }
}
