package com.tgerstel.quizmaster.domain.model;

import com.tgerstel.quizmaster.domain.dto.QuestionToTakeDTO;

import java.util.List;

public record Question(
        String id,
        String questionId,
        String question,
        String explanation,
        String tags,
        String author,
        Type type,
        ScoringStrategyType scoringStrategyType,
        Status status,
        Visibility visibility,
        List<Answer> answers,
        Long version
) {

    public QuestionToTakeDTO toTakeDTO() {
        return new QuestionToTakeDTO(questionId, question, answers.stream().map(Answer::toTakeDTO).toList());
    }

    public enum Type {
        SINGLE_CHOICE,
        MULTIPLE_CHOICE,
        TEXT, // open-ended question where the answer is a free-form text
        NUMBER, // open-ended question where the answer is a number
        BOOLEAN,
        RATING,
        SURVEY // non-scored question used for gathering feedback or opinions
    }

    public enum ScoringStrategyType {
        ALL_OR_NOTHING, // for single choice and multiple choice questions
        PARTIAL, // option for multiple choice questions where each correct answer contributes to the total score
        MANUAL, // requires manual grading by an instructor or administrator, suitable for open-ended questions
        NONE // for questions that are not scored, such as survey questions or feedback forms
    }

    public enum Status {
        DRAFT,
        PUBLISHED,
        ARCHIVED,
        DELETED
    }

    public enum Visibility {
        PRIVATE,
        PUBLIC,
        UNLISTED,
        SYSTEM
    }
}