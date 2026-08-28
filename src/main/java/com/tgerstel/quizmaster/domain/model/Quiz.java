package com.tgerstel.quizmaster.domain.model;

import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.dto.QuizToTakeDTO;
import com.tgerstel.quizmaster.domain.port.QuizTypePolicy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class Quiz implements QuizTypePolicy {
    private String id;
    private String title;
    private String description;
    private String author;
    private String ownerId;
    private Long version;
    private List<Question> questions;

    private Quiz.Type type;
    private Quiz.Status status;
    private Quiz.Visibility visibility;

    private Integer passRate;
    private boolean shuffleQuestions;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant publishedAt;

    public enum Type {
        EXAM,
        EXAM_OPEN, // for exams that allow open-ended questions and manual grading
        PRACTICE,
        SURVEY, // for collecting feedback or opinions, not necessarily scored
        ASSESSMENT, // for evaluating knowledge or skills, can be scored
        ASSESSMENT_OPEN, // for assessments that allow open-ended questions and manual grading
        POLL, // for quick feedback or opinions, not necessarily scored
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE,
        UNLISTED,
        SYSTEM
    }

    public enum Status {
        DRAFT,
        PUBLISHED,
        ARCHIVED,
        DELETED
    }

    public QuizToTakeDTO toTakeDTO(String sessionId) {
        var questionsToTake = questions.stream().map(Question::toTakeDTO).toList();
        return new QuizToTakeDTO(title, sessionId, id, questionsToTake);
    }

    @Override
    public boolean supports(QuestionDTO question) {

        var questionType = question.type();
        var strategyType = question.scoringStrategyType();

        return switch (type) {

            // Strict exam: only closed questions, fully auto-gradable
            case EXAM ->
                    switch (questionType) {
                        case SINGLE_CHOICE, BOOLEAN ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING;

                        case MULTIPLE_CHOICE ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.PARTIAL;

                        default -> false;
                    };

            // Exam with open-ended questions
            case EXAM_OPEN ->
                    switch (questionType) {
                        case SINGLE_CHOICE, BOOLEAN ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING;

                        case MULTIPLE_CHOICE ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.PARTIAL;

                        case TEXT, NUMBER ->
                                strategyType == Question.ScoringStrategyType.MANUAL;

                        default -> false;
                    };

            // Practice allows almost everything except surveys
            case PRACTICE ->
                    switch (questionType) {
                        case SINGLE_CHOICE, BOOLEAN -> strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING;
                        case MULTIPLE_CHOICE ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.PARTIAL;
                        case TEXT, NUMBER -> strategyType == Question.ScoringStrategyType.MANUAL;
                        case RATING -> strategyType == Question.ScoringStrategyType.NONE;
                        default -> false;
                    };

            // Pure feedback collection
            case SURVEY ->
                    switch (questionType) {
                        case SURVEY, RATING, TEXT, BOOLEAN -> strategyType == Question.ScoringStrategyType.NONE;
                        default -> false;
                    };

            // Skill/knowledge evaluation
            case ASSESSMENT ->
                    switch (questionType) {
                        case SINGLE_CHOICE, BOOLEAN, NUMBER ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING;
                        case MULTIPLE_CHOICE ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.PARTIAL;
                        default -> false;
                    };

            // Assessment with open questions
            case ASSESSMENT_OPEN ->
                    switch (questionType) {
                        case SINGLE_CHOICE, BOOLEAN -> strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING;
                        case MULTIPLE_CHOICE ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.PARTIAL;
                        case NUMBER ->
                                strategyType == Question.ScoringStrategyType.ALL_OR_NOTHING
                                        || strategyType == Question.ScoringStrategyType.MANUAL;
                        case TEXT -> strategyType == Question.ScoringStrategyType.MANUAL;
                        default -> false;
                    };

            // Quick opinion voting
            case POLL ->
                    switch (questionType) {
                        case SINGLE_CHOICE, MULTIPLE_CHOICE, BOOLEAN ->
                                strategyType == Question.ScoringStrategyType.NONE;
                        default -> false;
                    };
        };
    }
}
