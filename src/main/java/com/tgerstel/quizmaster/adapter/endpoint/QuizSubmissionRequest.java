package com.tgerstel.quizmaster.adapter.endpoint;

import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import com.tgerstel.quizmaster.domain.command.SubmitQuizCommand;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuizSubmissionRequest {
    private String quizId;
    private List<QuestionSolution> solutions;

    public SubmitQuizCommand toCommand() {
        return new SubmitQuizCommand(quizId, solutions);
    }

}
