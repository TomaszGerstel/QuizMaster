package com.tgerstel.quizmaster.domain.command;

import com.tgerstel.quizmaster.domain.model.QuestionSolution;
import org.bson.types.ObjectId;

import java.util.List;

public record SubmitQuizCommand(ObjectId quizId, List<QuestionSolution> solution, String sessionId) {

}
