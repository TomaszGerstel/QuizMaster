package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.Question;

import java.util.EnumSet;

public record QuestionFilter(
        EnumSet<Question.Status> statuses,
        EnumSet<Question.Visibility> visibilities
) {

    public static QuestionFilter assignable() {
        return new QuestionFilter(
                EnumSet.of(Question.Status.PUBLISHED),
                EnumSet.of(
                        Question.Visibility.PUBLIC,
                        Question.Visibility.SYSTEM
                )
        );
    }

    public static QuestionFilter editable() {
        return new QuestionFilter(
                EnumSet.of(
                        Question.Status.DRAFT,
                        Question.Status.PUBLISHED
                ),
                EnumSet.of(Question.Visibility.PUBLIC)
        );
    }
}