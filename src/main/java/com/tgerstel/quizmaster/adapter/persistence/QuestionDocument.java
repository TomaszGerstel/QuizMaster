package com.tgerstel.quizmaster.adapter.persistence;

import com.tgerstel.quizmaster.domain.command.CreateQuestionCommand;
import com.tgerstel.quizmaster.domain.dto.AnswerDTO;
import com.tgerstel.quizmaster.domain.dto.QuestionDTO;
import com.tgerstel.quizmaster.domain.model.EvalQuestion;
import com.tgerstel.quizmaster.domain.model.Question;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Data
@Document(collection = "question")
@NoArgsConstructor
public class QuestionDocument {
    private ObjectId id;
    private String questionId;
    private String question;
    private String tags;
    private String explanation;
    private String author;
    private String ownerId;
    private List<BaseAnswer> answers;

    private Question.Type  type;
    private Question.ScoringStrategyType scoringStrategyType;
    private Question.Visibility visibility;
    private Question.Status status;

    private String expectedAnswer;
    private BigDecimal correctNumber;
    private BigDecimal tolerance;
    private Boolean correctBoolean;
    private Integer ratingMin;
    private Integer ratingMax;

    private Long version;

//    public QuestionDocument(String question, String tags, String explanation, String author,
//                            List<BaseAnswer> answers, Long version) {
//        this.question = question;
//        this.tags = tags;
//        this.explanation = explanation;
//        this.author = author;
//        this.answers = answers;
//        this.version = version;
//
//        // temp
//        this.type = Question.Type.MULTIPLE_CHOICE;
//        this.scoringStrategyType = Question.ScoringStrategyType.ALL_OR_NOTHING;
//        this.visibility = Question.Visibility.PUBLIC;
//        this.status = Question.Status.PUBLISHED;
//    }

    public Question toDomain() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toAnswer).toList();
        var shuffledAnswers = new ArrayList<>(mappedAnswers);
        Collections.shuffle(shuffledAnswers);
        return new Question(
                id.toString(),
                questionId,
                question,
                explanation,
                tags,
                author,
                type,
                scoringStrategyType,
                status,
                visibility,
                shuffledAnswers,
                version
        );
    }

    public EvalQuestion toEvalQuestion() {
        var mappedAnswers = answers.stream().map(BaseAnswer::toEvalAnswer).toList();
        return new EvalQuestion(questionId, type, scoringStrategyType, explanation, mappedAnswers);
    }

    public QuestionDTO toDTO() {
        var mappedAnswers = answers == null
                ? List.<AnswerDTO>of()
                : answers.stream()
                .map(BaseAnswer::toAnswerDTO)
                .toList();

        return new QuestionDTO(
                questionId,
                question,
                tags,
                explanation,
                type,
                scoringStrategyType,
                author,
                version,
                mappedAnswers,
                expectedAnswer,
                correctNumber,
                tolerance,
                correctBoolean,
                ratingMin,
                ratingMax
        );
    }

    public static QuestionDocument create(CreateQuestionCommand command, Long version) {
        var document = new QuestionDocument();

        document.question = command.question();
        document.tags = command.tags();
        document.explanation = command.explanation();
        document.author = command.author();

        document.type = command.type();
        document.scoringStrategyType = command.scoringStrategyType();

        // TODO: later: visibility should set user (after auth 'private' has sense)
        // 'status': for admin in future?
//        document.visibility = command.visibility();
//        document.status = command.status();

        document.visibility = Question.Visibility.PUBLIC;
        document.status = Question.Status.PUBLISHED;

        document.expectedAnswer = command.expectedAnswer();
        document.correctNumber = command.correctNumber();
        document.tolerance = command.tolerance();
        document.correctBoolean = command.correctBoolean();

        document.ratingMin = command.ratingMin();
        document.ratingMax = command.ratingMax();

        document.answers = mapAnswers(command.answers());

        document.version = version;

        return document;
    }

    public QuestionDocument update(CreateQuestionCommand command) {
        this.question = command.question();
        this.tags = command.tags();
        this.explanation = command.explanation();
        this.author = command.author();

        // TODO: type shouldn't be change; and visibility? status?
//        this.type = command.type();
//        this.scoringStrategyType = command.scoringStrategyType();
//        this.visibility = command.visibility();
//        this.status = command.status();

        this.expectedAnswer = command.expectedAnswer();
        this.correctNumber = command.correctNumber();
        this.tolerance = command.tolerance();
        this.correctBoolean = command.correctBoolean();

        this.ratingMin = command.ratingMin();
        this.ratingMax = command.ratingMax();

        this.answers = mapAnswers(command.answers());

        return this;
    }

    private static List<BaseAnswer> mapAnswers(List<AnswerDTO> answers) {
        if (answers == null) {
            return List.of();
        }

        return IntStream.range(0, answers.size())
                .mapToObj(i -> {
                    var answer = answers.get(i);

                    var baseAnswer = new BaseAnswer();
                    baseAnswer.setNo(i + 1);
                    baseAnswer.setValue(answer.value());
                    baseAnswer.setCorrect(answer.isCorrect());

                    return baseAnswer;
                })
                .toList();
    }

}