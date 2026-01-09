package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.dto.QuizBasicDTO;
import com.tgerstel.quizmaster.domain.dto.QuizDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToSolveDTO;
import com.tgerstel.quizmaster.domain.dto.QuizEvalDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

@Data
@Document(collection = "quiz")
@NoArgsConstructor
@AllArgsConstructor
public class QuizDocument {
    @Id
    private ObjectId id;
    private String title;
    private boolean editable;

    @DBRef
    private Set<QuestionDocument> questions;

    @Version
    private Long version;

    public QuizBasicDTO toBasicDTO() {
        var count = questions.size();
        return new QuizBasicDTO(id.toString(), title, count);
    }

    public QuizToSolveDTO toSolveDTO() {
        var mappedQuestions = questions.stream().map(QuestionDocument::toQuestion).toList();
        var shuffledQuestions = new ArrayList<>(mappedQuestions);
        Collections.shuffle(shuffledQuestions);
        return new QuizToSolveDTO(id.toString(), title, null, shuffledQuestions);
    }

    public QuizDTO toDetailedDTO() {
        var mappedQuestions = questions.stream().map(QuestionDocument::toDTO).toList();
        return new QuizDTO(id.toString(), title, mappedQuestions);
    }

    public QuizEvalDTO toEvalDTO() {
        var mappedQuestions = questions.stream().map(QuestionDocument::toEvalQuestion).toList();
        return new QuizEvalDTO(id.toString(), mappedQuestions);
    }
}
