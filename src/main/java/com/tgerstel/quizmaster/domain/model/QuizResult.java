package com.tgerstel.quizmaster.domain.model;

import java.util.List;

public record QuizResult (String quizId, boolean isPositive, Integer quizScore,
                          Integer questionsCount, List<AnswerReportEntry> answersReport, Long attemptTimeInSeconds) {
}
