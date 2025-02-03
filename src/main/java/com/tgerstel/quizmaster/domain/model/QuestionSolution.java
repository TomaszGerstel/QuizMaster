package com.tgerstel.quizmaster.domain.model;

import java.util.List;

public record QuestionSolution(String questionId, List<Integer> answers) {
}
