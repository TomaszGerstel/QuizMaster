package com.tgerstel.quizmaster.domain.command;

import com.tgerstel.quizmaster.domain.model.QuestionSolution;

import java.util.List;

public record SubmitQuizCommand(List<QuestionSolution> solution, String sessionId) {

}
