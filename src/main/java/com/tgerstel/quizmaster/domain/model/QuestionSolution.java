package com.tgerstel.quizmaster.domain.model;

import org.bson.types.ObjectId;

import java.util.List;

public record QuestionSolution(
        String questionId,
        String explanation,
        List<Integer> answers
) {}
