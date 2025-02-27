package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.Question;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Document(collection = "question")
public class QuestionDocument {
    private String id;
    private String question;
    private List<BaseAnswer> answers;

    public Question toQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswer).toList();
        var shuffledAnswers = new ArrayList<>(mappedAnswers);
        Collections.shuffle(shuffledAnswers);
        return new Question(id, question, shuffledAnswers);
    }

    public EvalQuestion toEvalQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toEvalAnswer).toList();
        return new EvalQuestion(id, mappedAnswers);
    }
}