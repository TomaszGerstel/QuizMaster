package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.model.Answer;
import com.tgerstel.quizmaster.domain.model.EvalAnswer;
import lombok.Data;

@Data
public class BaseAnswer {
    private Integer no;
    private String value;
    private boolean isCorrect;

    public Answer toAnswer() {
        return new Answer(no, value);
    }

    public EvalAnswer toEvalAnswer() {
        return new EvalAnswer(no, isCorrect);
    }
}
