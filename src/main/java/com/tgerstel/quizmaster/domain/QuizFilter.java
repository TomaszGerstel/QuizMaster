package com.tgerstel.quizmaster.domain;

import com.tgerstel.quizmaster.domain.model.Quiz;

import java.util.EnumSet;

public record QuizFilter(
        EnumSet<Quiz.Status> statuses,
        EnumSet<Quiz.Visibility> visibilities
) {

    public static QuizFilter assignable() {
        return new QuizFilter(
                EnumSet.of(Quiz.Status.PUBLISHED),
                EnumSet.of(
                        Quiz.Visibility.PUBLIC,
                        Quiz.Visibility.SYSTEM
                )
        );
    }

    public static QuizFilter editable() {
        return new QuizFilter(
                EnumSet.of(
                        Quiz.Status.DRAFT,
                        Quiz.Status.PUBLISHED
                ),
                EnumSet.of(Quiz.Visibility.PUBLIC)
        );
    }

}
