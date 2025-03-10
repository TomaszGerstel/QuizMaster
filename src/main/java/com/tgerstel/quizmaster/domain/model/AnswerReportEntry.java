package com.tgerstel.quizmaster.domain.model;

import java.util.Set;

public record AnswerReportEntry(String questionId, Set<Integer> expectedAnswers, boolean positive) {
}
